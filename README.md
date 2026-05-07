# season-memory-core
Módulo core do jogo SeasonMemory, focado em performance com escalabidade, testável e de fácil manutenção, utilizando Clean Architecture e princípios de Data-Oriented Design
## v1.0.0-beta
### Características Técnicas:

- **Clean Architecture:** Separação rigorosa de camadas para garantir testabilidade e manutenção.
- **Clean Kotlin:** Código idiomático seguindo as melhores práticas da linguagem.
- **UI Agnóstica:** Lógica desacoplada de frameworks de interface, preparada para ser acoplada em qualquer ambiente JVM.
- **Performance-First:** Utilização de processamento paralelo e processamento massivo de dados com ECS (Entity Component System) e DOD (Data-Oriented Design).

### Funcionalidades do Core:

- **Algoritmo de Match Inteligente:** Validação automática de combinações baseada em regras dinâmicas de estratégia.
- **Geração de Grupos por Probabilidade:** Engine capaz de gerar pares, trios e quartetos de cartas com base em pesos probabilísticos configuráveis.
- **Gameplay de Estratégias Atômicas:** Sistema onde cada estratégia de jogo possui suas próprias regras e estados, funcionando de forma independente e isolada.

### Qualidade e Estabilidade:
<img width="911" height="477" alt="Captura de tela 2026-05-07 164940" src="https://github.com/user-attachments/assets/f4578621-b7bc-4442-9e12-340d0c093f38" />

### Roadmap / Futuro:
- Modularização para distribuição via JitPack/Maven.
- Suporte a PvP Local por enquanto