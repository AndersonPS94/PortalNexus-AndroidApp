# Portal Nexus - Teste Técnico Android

O **Portal Nexus** é um aplicativo Android nativo premium, desenvolvido para demonstrar competências avançadas em arquitetura, design e integração de sistemas. O projeto unifica o multiverso da API Rick and Morty com uma gestão corporativa de funcionários, tudo sob uma interface futurista inspirada em tendências como Linear e Stripe.

## 🚀 Tecnologias e Dependências

*   **Linguagem:** Java (Rigor técnico solicitado)
*   **Networking:** OkHttp3 (Chamadas puras para controle total)
*   **JSON Parsing:** Gson
*   **Image Loading:** Glide (com cache e transformações)
*   **Animações:** MotionLayout e MotionScene (Splash Premium)
*   **UI:** Material Design 3 (Android 15 ready), ConstraintLayout, ViewBinding
*   **Jetpack:** ViewModel, LiveData, Activity Result API (Câmera), Core-SplashScreen
*   **Testes:** JUnit 4, Mockito, Espresso (UI Tests)
*   **CI/CD:** GitHub Actions (Pipeline automatizado)

## 🏗️ Arquitetura e Padrões

O projeto segue rigorosamente os princípios de **Clean Architecture** e **MVVM (Model-View-ViewModel)**:

*   **View Layer:** Activities que observam mudanças de estado.
*   **ViewModel Layer:** Lógica de negócio e estado da UI preservado.
*   **Repository Layer:** Padrão Singleton mediando fontes de dados locais e remotas.
*   **Service Layer:** Abstração total da camada de rede (OkHttp3).
*   **SOLID & Clean Code:** Código desacoplado, testável e sem comentários desnecessários.

## 📂 Estrutura de Pastas

```text
app/src/main/java/com/example/portalnexus/
├── adapter/        # Adaptadores RecyclerView (DiffUtil)
├── data/
│   ├── model/      # Modelos POJO (Character, Employee)
│   └── repository/ # Repositórios (Singletons)
├── service/        # Remote Data Sources (OkHttp implementações)
├── ui/             # Camadas visuais organizadas por feature
│   ├── splash/     # Splash animada (MotionLayout)
│   ├── home/       # Landing page
│   ├── login/      # Autenticação real
│   ├── menu/       # Dashboard principal
│   ├── characters/ # Rick & Morty Integration
│   ├── profile/    # Detalhes Hero + Câmera
│   └── employees/  # CRUD de funcionários
├── utils/          # Helpers (Permission, Network, Dialogs)
└── viewmodel/      # Lógica reativa (LiveData)
```

## 🔄 Fluxo do Aplicativo

1.  **Splash:** Storyboard de 2000ms com abertura de portal.
2.  **Home:** Introdução imersiva ao nexo.
3.  **Login:** Autenticação via backend local (`admin@empresa.com` / `123456`).
4.  **Menu:** Navegação entre Exploração (API) e Gestão (CRUD).
5.  **Personagens:** Listagem com paginação (pág. 1-3) e filtros triplos dinâmicos.
6.  **Perfil:** Visão detalhada (12 campos) + Integração com Câmera e Compartilhamento.
7.  **Funcionários:** Gestão completa com fotos reais capturadas na hora.

## 🔌 Integração com Backend (Grails)

O app está configurado para consumir o backend local em **Grails**:
*   **URL Base:** `http://192.168.1.5:8080` (Ajustável em `Constants.java`)
*   **Endpoints:** 
    *   `POST /api/auth/login`
    *   `GET/POST/PUT/DELETE /api/funcionarios`
*   **Dica:** Utilize `adb reverse tcp:8080 tcp:8080` para testar com celular físico via USB.

## 🛠️ Como Executar

1.  Clone o projeto.
2.  Abra no **Android Studio Ladybug (ou superior)**.
3.  Sincronize o Gradle (JDK 17 recomendado).
4.  Certifique-se de que o backend local está rodando.
5.  Execute o app (`Run 'app'`).

## 💡 Decisões Técnicas de Elite

*   **Preservação de Estado:** Uso de `onSaveInstanceState` no RecyclerView para evitar perda de posição no scroll.
*   **Segurança:** Implementação de **Permission Rationale** para acesso à câmera.
*   **Performance:** Uso de **Singletons** nos repositórios para otimizar conexões OkHttp.
*   **Design System:** Grid de 8dp e raios consistentes (Linear/Stripe Style).

---
Desenvolvido com excelência por **Anderson Pereira dos Santos**.
