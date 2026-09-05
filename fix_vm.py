from pathlib import Path

base = Path('C:/Users/stand/Documents/hermes_project/sound-healing-app')

vm = (base / 'app/src/main/java/com/example/soundhealing/viewmodel/RandomSessionViewModel.kt').read_text()
# Replace the problematic line
vm = vm.replace(
    'AudioPlaybackService.startWithDelay(\n                getApplication(),\n                session,\n                session.startDelaySeconds * 1000L\n            )',
    'AudioPlaybackService.startWithDelay(\n                getApplication(),\n                session.soundType,\n                session.startDelaySeconds * 1000L\n            )'
)
(base / 'app/src/main/java/com/example/soundhealing/viewmodel/RandomSessionViewModel.kt').write_text(vm)
print(f'RandomSessionViewModel.kt fixed')
print(f'eq: {Path(\"app/src/main/java/com/example/soundhealing/viewmodel/RandomSessionViewModel.kt\").read_bytes().count(b\"=\")}')
