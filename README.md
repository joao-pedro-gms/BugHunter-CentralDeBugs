# BugHunter — Central de Bugs

App Android em **Java** para cadastrar e acompanhar bugs encontrados em sistemas:
registro com validação, lista rolável (RecyclerView), painel de totalização,
marcação como corrigido (toque) e exclusão (toque longo).

**Autor:** João Pedro G M Silva — Matrícula: 20251012000740 — Curso: ADS PUC Goiás

## Estrutura

- `model/Bug.java` — modelo (código, título, módulo, descrição, gravidade, status, imagem)
  e a lógica pura de geração de código `BUG-00N`, pontuação e contadores.
- `adapter/BugAdapter.java` — adapter com `BugViewHolder` + View Binding do cartão,
  interface de clique e de clique longo (encaminham o objeto `Bug`).
- `MainActivity.java` — formulário, painel, RecyclerView com `LinearLayoutManager`
  e os três eventos pontuais: `notifyItemInserted`, `notifyItemChanged`, `notifyItemRemoved`.
- `app/src/test/java/.../VerificacaoLogica.java` — verificação da lógica em JVM pura (sem Android).

## Como compilar

Recipe do ambiente desta máquina (JDK 21 + Gradle 8.13 + Android SDK):

```bash
export JAVA_HOME=$HOME/.local/jdk21
export ANDROID_HOME=$HOME/Android/Sdk
$HOME/.local/gradle/gradle-8.13/bin/gradle assembleDebug
```

APK gerado em: `app/build/outputs/apk/debug/app-debug.apk`

## Como rodar a verificação de lógica (JVM, sem emulador)

```bash
$HOME/.local/jdk21/bin/javac -d /tmp/verificacao \
  app/src/main/java/com/bughunter/centraldebugs/model/Bug.java \
  app/src/test/java/com/bughunter/centraldebugs/VerificacaoLogica.java
$HOME/.local/jdk21/bin/java -cp /tmp/verificacao com.bughunter.centraldebugs.VerificacaoLogica
```

Confere: geração `BUG-001/002/003` (inclusive após remoções, sem duplicar),
pontuação = gravidade × 10 por bug corrigido e os contadores do painel.

## O que foi validado

- `gradle clean assembleDebug` → **BUILD SUCCESSFUL** (APK gerado).
- Lógica de negócio verificada por teste JVM (15 verificações, todas OK).
- Smoke test de UI no emulador Android 15 (AVD `ads1251`): lista vazia,
  cadastro com validação, cartão inserido, painel atualizado, toque para
  corrigir, toque longo para remover (com Toast) — screenshots em `/tmp/bughunter_*.png`.

## Regras atendidas

Sem ListView, sem `findViewById`, sem `android:onClick`, sem banco de dados,
sem internet, RecyclerView fora de qualquer ScrollView e sem
`notifyDataSetChanged` para alterações individuais.
