# Portal Nexus — Teste Técnico Android Nativo

Aplicativo Android nativo em **Java** com Splash animada, autenticação via backend próprio (Grails + MySQL), listagem paginada da API pública Rick and Morty, tela de perfil com câmera nativa e módulo de CRUD de funcionários.

> **Backend (Grails + MySQL):** repositório/pasta separada `backend-grails`  
> *(Ajuste o link abaixo para o repositório público real do backend)*  
> Backend: [backend-grails](https://github.com/AndersonPS94/backend-grails)

---

## Tecnologias e dependências

| Área | Tecnologia |
|------|------------|
| Linguagem | Java |
| UI | Views/XML, Material Design 3, ConstraintLayout, ViewBinding |
| Arquitetura | MVVM (ViewModel + LiveData) |
| Networking | OkHttp3 |
| JSON | Gson |
| Imagens | Glide |
| Splash | MotionLayout + AndroidX Core SplashScreen |
| Câmera | Activity Result API + FileProvider |
| Listas | RecyclerView + DiffUtil |
| Testes | JUnit 4, Mockito, Espresso |
| CI/CD | GitHub Actions |
| SDK | compileSdk / targetSdk **34**, minSdk **24** |

---

## Arquitetura

O app segue **MVVM** com separação de camadas:

- **UI (`ui/`)** — Activities, layouts XML, observação de LiveData  
- **ViewModel (`viewmodel/`)** — Estado da tela e regras de apresentação  
- **Repository (`data/repository/`)** — Mediação de dados (Singleton)  
- **Service (`service/`)** — Chamadas HTTP com OkHttp3  
- **Model (`data/model/`)** — POJOs (Character, Employee, etc.)

```text
app/src/main/java/com/example/portalnexus/
├── adapter/          # RecyclerView + DiffUtil
├── data/
│   ├── model/
│   └── repository/
├── service/          # OkHttp (Auth, Character, Employee)
├── ui/
│   ├── splash/
│   ├── home/
│   ├── login/
│   ├── menu/
│   ├── characters/
│   ├── profile/
│   └── employees/
├── utils/
└── viewmodel/
```

---

## Fluxo do aplicativo

1. **Splash** — MotionLayout (~2s), identidade visual do app  
2. **Home** — Introdução e acesso ao login  
3. **Login** — Autenticação no backend (`admin@empresa.com` / `123456`) + sessão local opcional  
4. **Menu** — Personagens (API pública) e Funcionários (CRUD backend)  
5. **Personagens** — Paginação até 3 páginas, filtros (status, gênero, espécie) e busca por nome  
6. **Perfil** — Detalhes do personagem (≥10 campos), câmera nativa, POST simulado (jsonplaceholder)  
7. **Funcionários** — Listar, cadastrar, editar e excluir via REST local  

---

## Pré-requisitos

### App Android

- **Android Studio** Ladybug (ou superior)  
- **JDK 17** (recomendado; o módulo app compila com Java 11 bytecode)  
- Emulador Android **ou** dispositivo físico com depuração USB  

### Backend (obrigatório para login e CRUD de funcionários)

- **JDK 17+**  
- **MySQL 8.0+** (ou Docker)  
- Projeto **backend-grails** (Grails 7 + GORM)  

Instruções detalhadas de banco e Grails estão no README do backend. Resumo abaixo.

---

## Credenciais de teste

| Campo | Valor |
|-------|--------|
| E-mail | `admin@empresa.com` |
| Senha | `123456` |

> Use **exatamente** essas credenciais (mesmo valor do seed do backend e do fallback de demonstração do app).

---

## Base URL do backend no app

Arquivo: `app/src/main/java/com/example/portalnexus/utils/Constants.java`

```java
public static final String BASE_URL = "http://192.168.1.5:8080";
```

| Ambiente | URL sugerida | Observação |
|----------|--------------|------------|
| Emulador (AVD) | `http://10.0.2.2:8080` | IP padrão do host no emulador Google |
| Emulador (config atual) | `http://192.168.1.5:8080` | Valor padrão no código — ajuste se necessário |
| Celular + **adb reverse** | `http://localhost:8080` | Após `adb reverse tcp:8080 tcp:8080` |
| Celular + rede Wi-Fi | `http://IP_DA_MAQUINA:8080` | Ex.: `http://192.168.1.100:8080` |

O app já possui `network_security_config` permitindo HTTP (cleartext) para hosts locais (`localhost`, `10.0.2.2`, `192.168.1.5`, etc.). Inclua o IP da sua máquina nesse XML se usar rede local.

### Endpoints consumidos

| Método | Endpoint |
|--------|----------|
| `POST` | `/api/auth/login` |
| `GET` | `/api/funcionarios` |
| `POST` | `/api/funcionarios` |
| `PUT` | `/api/funcionarios/{id}` |
| `DELETE` | `/api/funcionarios/{id}` |

APIs públicas:

- Rick and Morty: `https://rickandmortyapi.com/api/character`  
- POST simulado (câmera): `https://jsonplaceholder.typicode.com/posts`  

---

## Como executar

### 1) Subir o backend (Grails + MySQL)

```bash
# MySQL (local ou Docker — ver README do backend)
mysql -u root -p < database/setup.sql
# ou: docker-compose up -d mysql

cd backend-grails
./gradlew bootRun
# Aguarde: http://localhost:8080
```

Teste rápido:

```bash
curl http://localhost:8080/api/funcionarios

curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@empresa.com","senha":"123456"}'
```

### 2) Emulador

1. Clone este repositório.  
2. Abra no **Android Studio**.  
3. Sincronize o Gradle.  
4. Confirme `Constants.BASE_URL` (`10.0.2.2` ou `192.168.1.5`).  
5. Com o backend em `localhost:8080`, execute **Run 'app'**.  

### 3) Celular físico — adb reverse (recomendado)

```bash
adb devices
adb reverse tcp:8080 tcp:8080
adb reverse --list
```

1. Ajuste `BASE_URL` para `http://localhost:8080` **ou** mantenha a porta redirecionada conforme a URL usada.  
2. Instale/execute o app no dispositivo.  
3. Login com `admin@empresa.com` / `123456`.  

### 4) Celular físico — rede local

1. Descubra o IP da máquina (`ipconfig` / `hostname -I`).  
2. Defina `BASE_URL = "http://SEU_IP:8080"`.  
3. Libere a porta 8080 no firewall, se necessário.  
4. Celular e PC na **mesma rede Wi-Fi**.  

---

## Funcionalidades implementadas (escopo do teste)

| Requisito | Status |
|-----------|--------|
| Splash Activity animada (MotionLayout) | ✅ |
| Tela inicial + Login com validação e erros | ✅ |
| Sessão local (“manter logado”) | ✅ |
| Menu com módulos Personagens e Funcionários | ✅ |
| Listagem Rick and Morty, até 3 páginas | ✅ |
| Filtros status / gênero / espécie (+ nome) | ✅ |
| Card com ≥5 informações + foto | ✅ |
| Perfil com ≥10 informações | ✅ |
| Câmera nativa + permissão + FileProvider | ✅ |
| POST simulado após captura | ✅ |
| CRUD funcionários via backend local | ✅ |
| Estados loading / erro / lista vazia | ✅ |
| Tema claro e **modo escuro** | ✅ |
| RecyclerView + DiffUtil | ✅ |
| OkHttp3 + Gson + Glide | ✅ |
| MVVM + Views/XML | ✅ |
| SDK 34 | ✅ |

---

## Decisões técnicas

- **MVVM** com LiveData para estado reativo da UI.  
- **OkHttp3** em todos os requests (backend e APIs públicas).  
- **DiffUtil** nos adapters de personagens e funcionários.  
- **SessionManager** (SharedPreferences) para token e “manter logado”.  
- **Permission rationale** e redirecionamento às configurações para câmera.  
- **FileProvider** para captura segura de foto.  
- Cache local de fotos de personagens/funcionários durante o fluxo.  
- CI com lint, testes unitários e geração de APK debug.  

---

## Testes

```bash
./gradlew testDebugUnitTest
./gradlew lintDebug
./gradlew assembleDebug
```

Há testes unitários (ViewModels, Repository, NetworkUtils) e instrumentados (login / navegação).

---

## Estrutura da entrega

Conforme o PDF do teste, a entrega pode ser **um ou dois repositórios**:

| Parte | Conteúdo |
|-------|----------|
| Este repositório | App Android (Portal Nexus) |
| `backend-grails` | API Grails + MySQL + README de ambiente |

Ambos devem estar **públicos** e o README de cada um deve permitir subir o fluxo completo.

---

## Itens pendentes / observações

- Ajuste fino de `BASE_URL` conforme emulador vs dispositivo (ver tabela acima).  
- Resposta de create/update do backend pode vir encapsulada em `funcionario`; o app trata o body de forma tolerante — validar o fluxo completo com o backend no ar.  
- Campo `dataCriacao` existe no backend; a UI do app prioriza os campos principais do formulário/lista.  

*(Remova ou atualize esta seção se tudo estiver validado ponta a ponta.)*

---

## Checklist mínimo para o avaliador

1. Subir MySQL + `backend-grails` (`./gradlew bootRun`)  
2. Conferir login via `curl`  
3. Abrir o app no Android Studio e sincronizar Gradle  
4. Emulador **ou** `adb reverse tcp:8080 tcp:8080`  
5. Login: `admin@empresa.com` / `123456`  
6. Personagens (paginação + filtros + perfil + câmera)  
7. Funcionários (listar / criar / editar / excluir)  

---

## Licença / autor

Desenvolvido para o **Teste Técnico — Android Nativo Júnior** por **Anderson Pereira dos Santos**.
