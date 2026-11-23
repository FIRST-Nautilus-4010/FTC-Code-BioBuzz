# Proyecto completo (Intake + Shooter + Drive) con OpModes de prueba

Contiene TODAS las clases necesarias para compilar y probar. Paquete: `org.firstinspires.ftc.teamcode`.

```
Robot.java, SubsystemManager.java
opmodes/teleOp.java                      <- TeleOp único (alianza elegida en INIT)
opmodes/tests/                           <- IntakeTest, ShooterKvTest, ShooterTuner, SequenceTest
pedro/Constants.java                     <- STUB (Pedro sin configurar). No sobrescribas el tuyo si ya existe
subsystems/drive/DriveSubsystem.java
subsystems/intake/{IntakeIo, IntakeSubsystem, actions/IntakeActions}.java
subsystems/shooter/{ShooterIo, ShooterSubsystem, actions/Actions}.java
utils/{Constants (@Config), Alliance, EndPoseSave, FieldPoses, States}.java
```

## Instalación
`build.dependencies.gradle`:
```
repositories { maven { url 'https://maven.brott.dev/' } }              // además de los que ya tienes
dependencies { implementation 'com.acmerobotics.dashboard:dashboard:0.5.1' }
```
(el resto de tus dependencias: Pedro 3, NextFTC v2 / Ivy, igual que ahora; recuerda `com.pedropathing.ivy:pedro` si no lo tienes)

Dashboard: conéctate al WiFi del Control Hub y abre http://192.168.43.1:8080/dash
- Configuration: edita `Constants` y las variables de cada test EN VIVO. ⚠ No se guardan: copia el número al archivo.
- Graph: arrastra "RPM objetivo" y "RPM medida".

## Orden de pruebas (OpModes "Test | ...")
1. **Intake**: dirección (`INTAKE_REVERSED`), ticks por vuelta (girar el eje de salida 1 vuelta a mano), RPM de recoger/escupir.
2. **Shooter Tuner**: dirección (`SHOOTER_REVERSED`), verifica RPM (`SHOOTER_TICKS_PER_REV`, `SHOOTER_RATIO`).
3. **Shooter kV Sweep**: barrido 4–10 V -> `SHOOTER_KV`.
4. **Shooter Tuner**: `SHOOTER_KP`, `SHOOTER_KI`, tolerancia; RT simula un disparo (caída y recuperación); RPM de tiro cercana/lejana.
5. **Secuencias (manager)**: SPINUP -> SHOOT -> STOP; ajusta `FEED_TIME_S`, `SPINUP_TIMEOUT_S`.
6. Drive y `teleOp` completo: cuando Pedro esté configurado.

## Cambios respecto a tu último zip
- `utils/Constants`: `@Config`, sin `final`, valores iniciales razonables; nuevos `INTAKE_REVERSED`, `SHOOTER_REVERSED`; quitado `SHOOTER_RPM` (ahora `getRpm()` usa `SHOOTER_TICKS_PER_REV * SHOOTER_RATIO`).
- `IntakeIo` / `ShooterIo`: dirección desde `Constants`; métodos de diagnóstico (ticks crudos, ticks/s, `setPower`).
- `Alliance`: rotación de 180° (x' = 141.5 - x, y' = 141.5 - y, heading + π) en vez de espejo; `mirrorHeading` -> `rotateHeading`.
- `FieldPoses`: ejemplos del lado AZUL (x > 70.75) y con `PARK` propio. Mídelos en tu campo.
- `DriveSubsystem`: usa `rotateHeading`; quitado import sin usar.
- `SubsystemManager`: `goShootSequence` marca `drivenSequence = true`; `driverOverride()` también cubre `PARK`.
- `teleOp`: usa `manager.driverOverride()`; el aviso de desacuerdo de alianza ya no se borra; `logAlliance()` también en `periodic()`; `EndPoseSave.clear()` en `end()`.
- `Robot`: `getMechanisms()` devuelve el set ya creado.
