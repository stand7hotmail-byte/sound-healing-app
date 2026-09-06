from pathlib import Path

base = Path('C:/Users/stand/Documents/hermes_project/sound-healing-app')

# Read current MainScreen
ms = (base / 'app/src/main/java/com/example/soundhealing/ui/screen/MainScreen.kt').read_text()

# Fix DisposableEffect
ms = ms.replace(
    '''DisposableEffect(Unit) {
        onDispose { viewModel.stopAll() }
        doLast { }
    }''',
    '''DisposableEffect(Unit) {
        onDispose { viewModel.stopAll() }
    }'''
)

# Fix UiState references - check what fields exist
# isPlaying -> needs to be checked
# currentSound -> needs to be checked

# Let's just show the errors and fix them one by one
print('Current file loaded')
print(f'Has isPlaying: {\"isPlaying\" in ms}')
print(f'Has currentSound: {\"currentSound\" in ms}')
print(f'Has startSound: {\"startSound\" in ms}')
print(f'Has stopSound: {\"stopSound\" in ms}')
