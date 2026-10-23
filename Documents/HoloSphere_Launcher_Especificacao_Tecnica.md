# HoloSphere Launcher — Especificação técnica e plano de desenvolvimento

**Versão:** 1.0  
**Plataforma:** Android 10+ (API 29; validar matriz de compatibilidade)  
**Stack:** Kotlin, Jetpack Compose, Android Graphics/Canvas, Room, DataStore, Hilt, Coroutines/Flow  
**Objetivo:** launcher Android radicalmente diferente, com aplicações distribuídas numa esfera holográfica navegável por toque, mas com consumo reduzido de recursos.

> **Princípio central:** criar a ilusão de uma esfera 3D sem exigir um motor de jogo permanente. A interface deve continuar utilizável em modo 2D, sem sensores e offline.

## 1. Visão do produto

O ecrã inicial apresenta hora, data e meteorologia numa área superior; a zona central mostra uma esfera de ícones distribuídos em torno de um globo virtual. O utilizador arrasta a esfera para rodar, toca num ícone para abrir uma aplicação e pode aproximar-se de categorias (Comunicação, Multimédia, Trabalho, Ferramentas, Jogos). A pesquisa e uma lista alfabética estão sempre a um gesto de distância.

**Diferenciação:** navegação espacial; profundidade e oclusão suaves; categorias como constelações; personalização visual profunda; animações apenas durante interação; acesso rápido convencional para não prejudicar a usabilidade.

### Objetivos mensuráveis (metas, não garantias)
- Primeira interação fluida após arranque a quente: alvo < 350 ms em dispositivo de referência.
- Renderização: alvo de 60 fps durante gestos num dispositivo intermédio; reduzir efeitos se necessário.
- CPU em repouso: sem loop de renderização contínuo; sem serviço foreground persistente.
- Memória: estabelecer baseline com `dumpsys meminfo` e perfis de referência; otimizar sem assumir um valor universal.
- Offline: navegação, aplicações e personalização funcionam sem rede.
- Acessibilidade: alternativa em lista, fontes ajustáveis, suporte a TalkBack e opção de reduzir movimento.

## 2. Experiência e ecrãs

### 2.1 Home / HoloSphere
- Topo: relógio, data e cartão meteorológico opcional.
- Centro: esfera com 8–24 ícones visíveis; os restantes aparecem por rotação ou pesquisa.
- Fundo: cor sólida, gradiente, imagem local ou wallpaper do sistema; desfoque opcional.
- Rodapé: pesquisa, acesso à lista de aplicações, atalhos fixos e botão de edição.
- Estados: vazio, primeiro arranque, carregamento de ícones, sem localização, sem Internet.

### 2.2 Visão de categorias
- Categorias representadas por órbitas/constelações com cor e rótulo.
- Tocar numa categoria expande os seus ícones; gesto de voltar recolhe a vista.
- Permitir categorias manuais, automáticas por tipo e híbridas; aplicações sem categoria vão para «Outras».

### 2.3 Todas as aplicações
- Lista alfabética, pesquisa instantânea, recentes e favoritas.
- Pesquisa por nome normalizado (acentos/capitalização), sem enviar dados para servidores.
- Pressão longa: fixar, atribuir categoria, informação da aplicação e desinstalar via intent do sistema.

### 2.4 Editor da esfera
- Adicionar/remover apps, alterar tamanho, densidade e distribuição de ícones.
- Escolher estilo: Esfera, Anéis, Galáxia, Minimal e Constelações.
- Pré-visualização antes de guardar; botão «Repor predefinições».

### 2.5 Personalização
- Fundos: imagem da galeria via Photo Picker, wallpaper atual quando acessível, gradiente, cor.
- Ajustes: luminosidade, vinheta, escurecimento, desfoque (com fallback), parallax opcional.
- Aparência: tema claro/escuro/automático, paleta, brilho da esfera, rasto, partículas, tipografia, rótulos.
- Movimento: sensibilidade, inércia, velocidade máxima, animações reduzidas, desativar sensores.
- Disposição: posição e escala da esfera, densidade, dock, cartões superiores, margens.

### 2.6 Widgets e informação
- Widgets próprios: relógio, meteorologia, agenda, bateria e próximo evento.
- Widgets de terceiros: `AppWidgetHost` e `AppWidgetManager`, com fluxo de autorização e configuração do fornecedor; mostrar em painel próprio no MVP avançado.
- Meteorologia: Open-Meteo com cache e consentimento para localização; cidade manual sem permissão.
- Calendário: `CalendarContract` com `READ_CALENDAR` opcional e explicação contextual.

### 2.7 Definições e manutenção
- Importar/exportar definições (JSON versionado, sem credenciais).
- Diagnóstico: FPS durante interação, contagem de ícones, cache e duração do arranque (opção de programador).
- Permissões e privacidade; informação da versão; restauro do launcher anterior.

## 3. Interações e gestos

| Gesto | Ação | Observações |
|---|---|---|
| Arrastar horizontal/vertical | Rodar a esfera | Velocidade proporcional ao gesto |
| Soltar | Inércia com desaceleração | Desligável para reduzir movimento |
| Toque em ícone | Abrir aplicação | Feedback visual e tátil opcional |
| Pressão longa | Menu contextual | Não interferir com gesto de rotação |
| Pinça | Zoom da esfera | Limites min/max configuráveis |
| Deslizar para cima fora da esfera | Todas as aplicações | Compatível com gestos do sistema |
| Toque na lupa | Pesquisa | Teclado abre apenas quando necessário |
| Voltar | Regressar à vista anterior | Respeitar navegação preditiva quando aplicável |

**Conflitos:** reservar áreas de margem para gestos de navegação do Android; não sequestrar gestos de sistema. Implementar thresholds de toque/arrasto e testes com TalkBack.

## 4. Arquitetura

```text
app (Activity HOME, navegação, injeção de dependências)
  ├── feature/home (HoloSphereScreen, HomeViewModel)
  ├── feature/apps (AppDrawerScreen, SearchViewModel)
  ├── feature/categories (CategoryScreen, CategoryViewModel)
  ├── feature/editor (SphereEditorScreen, EditorViewModel)
  ├── feature/settings (SettingsScreen)
  ├── feature/widgets (WidgetHostScreen)
  ├── sphere-engine (projeção, rotação, hit testing, animação)
  ├── domain (models, use cases, repositories)
  ├── data (Room, DataStore, PackageManager, Open-Meteo, CalendarContract)
  └── background (WorkManager, atualização de cache)
```

**Estratégia gráfica:** MVP com `Canvas` do Compose, matrizes/quaterniões calculados em Kotlin e projeção 3D→2D. Desenhar ícones como bitmaps em cache, com escala e opacidade dependentes da profundidade. Evitar OpenGL/Vulkan no MVP; avaliar `GLSurfaceView`/OpenGL ES apenas se medições reais justificarem.

**Camadas:** `Presentation → Domain → Data`; o motor da esfera recebe modelos imutáveis e produz posições projetadas e regiões de toque. Não acoplar cálculo geométrico a acesso a dados.

## 5. Algoritmo da esfera

### 5.1 Distribuição inicial
Distribuir N ícones com *Fibonacci sphere* para evitar aglomerados nos polos:

```kotlin
fun fibonacciSphere(count: Int): List<Vec3> {
    if (count <= 0) return emptyList()
    val goldenAngle = Math.PI * (3.0 - kotlin.math.sqrt(5.0))
    return (0 until count).map { i ->
        val y = 1.0 - 2.0 * (i + 0.5) / count
        val radius = kotlin.math.sqrt(1.0 - y * y)
        val theta = goldenAngle * i
        Vec3(radius * kotlin.math.cos(theta), y, radius * kotlin.math.sin(theta))
    }
}
```

### 5.2 Transformação e projeção
1. Guardar orientação como quaternion para evitar *gimbal lock*.
2. Aplicar rotação às posições base.
3. Projetar em 2D com perspetiva suave: `scale = focal / (focal + zOffset)` com limites seguros.
4. Ordenar por profundidade (desenhar primeiro os ícones de trás).
5. Calcular hit testing do ícone visível mais próximo do toque.
6. Aplicar alpha/blur/escala com base na profundidade; evitar processamento pesado por frame.

### 5.3 Física e desempenho
- Atualizar animação apenas quando houver gesto, inércia ou transição; terminar quando velocidade < epsilon.
- Usar `Animatable`/`withFrameNanos` com cancelamento ao perder foco; nunca usar `while(true)` permanente.
- Reduzir número de ícones renderizados (frustum/hemisfério visível) e pré-calcular texturas.
- Se necessário, permitir modo estático e lista 2D.

## 6. Integração Android como launcher

Manifest mínimo ilustrativo:

```xml
<activity android:name=".MainActivity" android:exported="true">
  <intent-filter>
    <action android:name="android.intent.action.MAIN" />
    <category android:name="android.intent.category.HOME" />
    <category android:name="android.intent.category.DEFAULT" />
  </intent-filter>
</activity>
```

- Obter aplicações lançáveis via `LauncherApps` ou `PackageManager` conforme perfis e visibilidade de pacotes; declarar apenas as consultas necessárias no manifesto.
- Abrir apps através de `LauncherApps.startMainActivity` ou intents lançáveis apropriadas, com tratamento de apps removidas e perfis de trabalho.
- Usar `RoleManager`/definições do sistema para pedir seleção como launcher predefinido quando suportado; nunca forçar.
- Ouvir alterações de pacotes para atualizar a cache, evitando varrimentos frequentes.
- Verificar restrições de políticas da Google Play relativas à visibilidade de aplicações (`QUERY_ALL_PACKAGES` apenas quando elegível e justificado).
- A app não substitui livremente a barra de estado, ecrã de bloqueio ou navegação do sistema.

## 7. Modelo de dados

| Entidade | Campos essenciais | Armazenamento |
|---|---|---|
| `LauncherSettings` | theme, wallpaperUri, blur, sphereStyle, motionEnabled, labelsEnabled | DataStore |
| `PinnedApp` | id, packageName, componentName, userProfile, sortOrder, categoryId | Room |
| `Category` | id, name, color, icon, sortOrder, isSystem | Room |
| `SphereNode` | id, appId, x, y, z, scaleOverride, categoryId | Room |
| `GestureBinding` | gesture, action, enabled | Room/DataStore |
| `WeatherCache` | locationKey, tempC, code, fetchedAt, expiresAt | Room |
| `WidgetInstance` | appWidgetId, provider, panelId, position, size | Room |
| `BackupMetadata` | schemaVersion, createdAt, appVersion | JSON export |

**Migrações:** definir `Room` migrations desde a versão 1. `wallpaperUri` deve manter permissão persistente quando obtida por seletor que a suporte; caso contrário, copiar imagem selecionada para armazenamento privado da app.

## 8. Permissões e privacidade

- `INTERNET` e `ACCESS_NETWORK_STATE`: meteorologia opcional.
- `ACCESS_COARSE_LOCATION`/`ACCESS_FINE_LOCATION`: opcionais, apenas se o utilizador pedir localização automática.
- `READ_CALENDAR`: opcional para eventos.
- Widgets externos: fluxo de bind autorizado pelo sistema, sem permissões genéricas adicionais inventadas.
- Sem conta obrigatória, sem analytics por defeito, sem envio de lista de aplicações para servidores.
- Dados locais; exportação explícita e exclusão nas definições.

## 9. Configurações completas (catálogo)

| Grupo | Configurações |
|---|---|
| Esfera | raio, zoom, posição X/Y, inclinação inicial, densidade, distribuição, categorias, tamanho dos ícones |
| Movimento | inércia, fricção, sensibilidade, vibração, reduzir movimento, FPS máximo durante interação |
| Ícones | packs compatíveis, forma, tamanho, rótulos, opacidade, sombra, indicador de notificações quando permitido |
| Fundo | imagem própria, gradiente, cor, recorte, escurecimento, blur, parallax, fundos por perfil |
| Tema | claro/escuro/automático, cor principal, brilho, contrastes, fonte e tamanho |
| Home | relógio, data, meteorologia, dock, pesquisa, cartões e ordem |
| Aplicações | ocultar da interface, favoritas, categorias, ordem, recentes, pesquisa |
| Gestos | arrastar, pinça, duplo toque, pressão longa, ações e sensibilidade |
| Widgets | adicionar, mover, redimensionar, remover, permissões e refresh |
| Acessibilidade | modo 2D, alto contraste, texto grande, TalkBack, reduzir animações |
| Dados | backup/restauro, limpar cache, reposição, privacidade e diagnóstico |

## 10. Estratégia de baixo consumo

1. **Zero render loop em repouso:** desenhar apenas por alterações de estado e interação.
2. **Cache LRU:** ícones carregados em tamanho adequado, limite de memória, invalidar por atualização de pacote.
3. **Sem polling:** reagir a eventos do sistema; meteorologia via WorkManager com frequência razoável (ex.: 30–60 min).
4. **Sem sensores contínuos:** giroscópio opcional apenas enquanto o ecrã estiver visível e a opção ativa.
5. **Efeitos escaláveis:** reduzir partículas, brilho e profundidade conforme perfil de desempenho.
6. **Medições reais:** Macrobenchmark, Perfetto, Android Studio Profiler, `dumpsys gfxinfo` e `dumpsys meminfo`.
7. **Perfis:** Económico (sem partículas/parallax), Equilibrado e Visual (efeitos limitados).

## 11. Roadmap de implementação

### Fase 0 — Prova de conceito (1 semana)
- Esfera Canvas com ícones simulados, rotação e hit testing.
- Testar 20/50/100 ícones; medir FPS e memória.
- **Aceitação:** rotação sem saltos, ícone selecionado corretamente e repouso sem animação contínua.

### Fase 1 — MVP Launcher (2 semanas)
- Registo HOME, lista de aplicações reais, abertura, pesquisa e favoritas.
- Esfera funcional e fallback em lista; persistência de configuração.
- **Aceitação:** utilizador consegue usar o dispositivo diariamente sem ficar preso na interface.

### Fase 2 — Categorias e personalização (2 semanas)
- Categorias, editor, fundos próprios, temas, tamanho dos ícones e gestos.
- **Aceitação:** personalizações mantêm-se após reinício e atualização da app.

### Fase 3 — Informação e widgets (2 semanas)
- Relógio, meteorologia, agenda opcional, cache e painel de widgets de terceiros.
- **Aceitação:** dados corretos, permissões recusadas sem crash e funcionamento offline básico.

### Fase 4 — Otimização e qualidade (1–2 semanas)
- Perfis de desempenho, acessibilidade, testes de regressão, backup e testes em Android 10–16.
- **Aceitação:** métricas documentadas, sem ANRs conhecidos, consumo em repouso baixo e navegação por TalkBack possível.

**Estimativa total:** 8–10 semanas de desenvolvimento individual; rever após a prova de conceito.

## 12. Backlog técnico inicial

| ID | Prioridade | História/tarefa |
|---|---|---|
| HS-001 | P0 | Criar projeto Kotlin/Compose e arquitetura base |
| HS-002 | P0 | Implementar distribuição Fibonacci e projeção |
| HS-003 | P0 | Rotação por toque e inércia com paragem |
| HS-004 | P0 | Hit testing e abertura de apps |
| HS-005 | P0 | Registar como launcher HOME |
| HS-006 | P0 | Listar apps e pesquisar |
| HS-007 | P0 | Persistir favoritas e configurações |
| HS-008 | P1 | Editor de categorias e órbitas |
| HS-009 | P1 | Fundos personalizados e temas |
| HS-010 | P1 | Acessibilidade e modo 2D |
| HS-011 | P1 | Meteorologia com cache |
| HS-012 | P2 | Calendário e widgets externos |
| HS-013 | P1 | Benchmark e perfil económico |
| HS-014 | P2 | Backup/restauro versionado |

## 13. Plano de testes

- **Unitários:** geometria, ordenação por profundidade, seleção de ícones, regras de categorias e migrações.
- **UI:** rotação, zoom, pesquisa, permissões, voltar, orientação, escala de fontes, TalkBack.
- **Integração:** apps instaladas/desinstaladas, perfis de trabalho, troca de launcher, reboot, atualização de pacote.
- **Desempenho:** arranque frio/quente, gestos de 30 segundos, repouso de 10 minutos, 20/50/100/200 apps.
- **Resiliência:** sem rede, sem localização, sem calendário, low memory, modo poupança de energia.
- **Segurança:** intents validadas, importação JSON com limites e esquema, sem execução de comandos externos.

## 14. Critérios de aceitação do MVP

- [ ] Pode ser escolhido como launcher predefinido.
- [ ] Apresenta e abre as aplicações instaladas.
- [ ] Esfera roda e permite selecionar ícones sem toques falsos frequentes.
- [ ] Pesquisa e lista alfabética disponíveis como alternativa.
- [ ] Favoritas e definições sobrevivem ao reinício.
- [ ] Não existe animação contínua em repouso.
- [ ] Funciona sem Internet.
- [ ] Não bloqueia o acesso às definições do Android nem à troca de launcher.
- [ ] Possui modo de acessibilidade com navegação em lista.
- [ ] Inclui testes de desempenho em pelo menos dois dispositivos/perfis.

## 15. Decisões técnicas pendentes

1. **Motor visual:** manter Canvas Compose se a prova de conceito cumprir as metas; só migrar para OpenGL ES após benchmark.
2. **Ícones:** usar ícones de apps reais; packs de ícones como funcionalidade posterior e dependente dos formatos suportados.
3. **Widgets externos:** painel separado antes de tentar projeção em superfícies 3D.
4. **Distribuição:** APK privado primeiro; rever políticas Google Play antes de publicar.
5. **Nome final:** HoloSphere Launcher (confirmar disponibilidade de marca e package ID).

---

**Recomendação de implementação:** iniciar pelo protótipo da esfera com ícones reais, pesquisa e fallback em lista. Só depois investir em efeitos holográficos: a originalidade visual não deve comprometer a autonomia, a acessibilidade nem a fiabilidade de um launcher usado diariamente.
