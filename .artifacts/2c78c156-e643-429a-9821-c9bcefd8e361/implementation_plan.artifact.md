# Plano de Implementação - Correção de Formatação de Salário e Integração de Temas

Este plano descreve as ações necessárias para corrigir o bug de formatação de moeda (R$) e completar a integração dos temas Claro e Escuro no Portal Nexus.

## User Review Required

> [!IMPORTANT]
> A correção do bug de salário mudará a forma como os valores são extraídos dos campos de texto. Certifique-se de que todos os dados de salário existentes no banco de dados (ou API) estejam no formato numérico esperado (double/float).

## Open Questions

Nenhuma no momento. As causas técnicas do bug e as lacunas no tema foram identificadas.

## Proposed Changes

### [Utils]

#### [MODIFY] [CurrencyTextWatcher.java](file:///Users/andersonpereiradossantos/AndroidStudioProjects/PortalNexus/app/src/main/java/com/example/portalnexus/utils/CurrencyTextWatcher.java)
- Corrigir o método `parseCurrencyValue` para extrair corretamente o valor numérico de strings formatadas em PT-BR (ex: "R$ 20.000,00").
- A nova lógica irá remover todos os caracteres não numéricos e dividir por 100, garantindo consistência com a formatação do `TextWatcher`.

### [Resources - Colors]

#### [MODIFY] [colors.xml (values)](file:///Users/andersonpereiradossantos/AndroidStudioProjects/PortalNexus/app/src/main/res/values/colors.xml)
- Adicionar cores semânticas para os gradientes (ex: `gradient_start`, `gradient_center`, `gradient_end`).
- Mover cores de "glow" e "glass" para nomes mais padronizados se necessário.

#### [MODIFY] [colors.xml (values-night)](file:///Users/andersonpereiradossantos/AndroidStudioProjects/PortalNexus/app/src/main/res/values-night/colors.xml)
- Definir versões escuras para as cores de gradiente para garantir legibilidade e conforto visual no modo noturno.
- Garantir que `primary` e outras cores de destaque tenham contraste suficiente.

### [Resources - Drawables]

#### [MODIFY] [bg_splash_gradient.xml](file:///Users/andersonpereiradossantos/AndroidStudioProjects/PortalNexus/app/src/main/res/drawable/bg_splash_gradient.xml)
- Substituir cores hardcoded (#HEX) por referências `@color/...`.

#### [MODIFY] [bg_portal_circle.xml](file:///Users/andersonpereiradossantos/AndroidStudioProjects/PortalNexus/app/src/main/res/drawable/bg_portal_circle.xml)
- Substituir cores hardcoded por referências `@color/...`.

### [Resources - Layouts]

#### [MODIFY] [activity_home.xml](file:///Users/andersonpereiradossantos/AndroidStudioProjects/PortalNexus/app/src/main/res/layout/activity_home.xml)
- Verificar o uso de `glass_white` e `white` para garantir que o texto permaneça legível sobre o gradiente em ambos os temas.

---

## Verification Plan

### Automated Tests
- Não há testes unitários no momento, mas proporei a criação de um teste simples para `CurrencyTextWatcher.parseCurrencyValue` no diretório de testes.

### Manual Verification
1. Abrir o formulário de funcionário.
2. Inserir "2000000" (deve formatar como R$ 20.000,00).
3. Salvar e verificar se o valor salvo é 20000.0 (e não 2 milhões).
4. Alternar entre tema claro e escuro no sistema/emulador e verificar:
   - Splash screen.
   - Tela de login.
   - Menu principal.
   - Lista de funcionários.
   - Gradientes de fundo.
