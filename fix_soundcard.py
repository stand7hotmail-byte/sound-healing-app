from pathlib import Path

base = Path('C:/Users/stand/Documents/hermes_project/sound-healing-app')

# Read SoundCard.kt
sc = (base / 'app/src/main/java/com/example/soundhealing/ui/component/SoundCard.kt').read_text()
lines = sc.split('\n')

# Find and replace lines 23-28 (the when block)
new_lines = []
skip_until = -1
for i, line in enumerate(lines):
    if i < skip_until:
        continue
    # Replace the when block
    if 'val (emoji, name, description)=when' in line:
        # Replace entire block until closing brace
        new_lines.append('val display = when (soundType) {')
        new_lines.append('    is SoundType.Solfeggio -> soundType.frequency.displayData')
        new_lines.append('    is SoundType.Nature -> soundType.sound.displayData')
        new_lines.append('    is SoundType.Brainwave -> DisplayData(soundType.type.label, "${soundType.type.frequencyRange} - ${soundType.type.purpose}", "🧠")')
        new_lines.append('}')
        new_lines.append('val (emoji, name, description) = Triple(display.emoji, display.title, display.description)')
        # Skip old lines until closing brace
        j = i + 1
        while j < len(lines) and '}' not in lines[j]:
            j += 1
        skip_until = j
        continue
    new_lines.append(line)

sc = '\n'.join(new_lines)
# Ensure DisplayData import exists
if 'import com.example.soundhealing.domain.DisplayData' not in sc:
    sc = sc.replace('import com.example.soundhealing.domain.SoundType', 
                   'import com.example.soundhealing.domain.DisplayData\nimport com.example.soundhealing.domain.SoundType')
(base / 'app/src/main/java/com/example/soundhealing/ui/component/SoundCard.kt').write_text(sc)
print(f'SoundCard.kt: {len(new_lines)} lines')
print(f'Has displayData: {\"displayData\" in sc}')
print(f'Has Triple(soundType.type.emoji: {\"Triple(soundType.type.emoji\" in sc}')
