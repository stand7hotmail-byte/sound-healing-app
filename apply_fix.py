from pathlib import Path

base = Path('C:/Users/stand/Documents/hermes_project/sound-healing-app')

# Fix 1: RandomSession startDelaySeconds
rs = base / 'app/src/main/java/com/example/soundhealing/domain/RandomSession.kt'
rs_text = rs.read_text()
rs_text = rs_text.replace('(0..30).random()', '(0..5).random()')
rs.write_text(rs_text)
print(f'RandomSession.kt: startDelaySeconds fixed')
print(f'  eq: {rs.read_bytes().count(b\"=\")}')

# Fix 2: Add logs to RandomSessionViewModel
vm = base / 'app/src/main/java/com/example/soundhealing/viewmodel/RandomSessionViewModel.kt'
vm_text = vm.read_text()
# Add log before sessions.forEach
if 'Session:' not in vm_text:
    vm_text = vm_text.replace(
        'sessions.forEach { session ->\n AudioPlaybackService.startWithDelay(',
        'Log.d(TAG, \"Start playing ${sessions.size} sessions\")\n sessions.forEach { session ->\n Log.d(TAG, \"  Session: ${session.soundType}, delay=${session.startDelaySeconds}s\")\n AudioPlaybackService.startWithDelay('
    )
vm.write_text(vm_text)
print(f'RandomSessionViewModel.kt: logs added')
print(f'  eq: {vm.read_bytes().count(b"=")}')
