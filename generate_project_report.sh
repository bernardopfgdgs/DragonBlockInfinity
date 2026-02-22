#!/bin/bash

OUTPUT="PROJECT_COMPLETE.txt"
> "$OUTPUT"

echo "=====================================" >> "$OUTPUT"
echo "DRAGONBLOCKINFINITY - PROJETO COMPLETO" >> "$OUTPUT"
echo "=====================================" >> "$OUTPUT"
echo "" >> "$OUTPUT"

echo "## ESTRUTURA DO PROJETO (TREE)" >> "$OUTPUT"
echo "" >> "$OUTPUT"
tree -I 'build|gradle-8.1.1|.gradle' --charset ascii . >> "$OUTPUT" 2>/dev/null || find . -not -path '*/\.*' -not -path '*/build*' -not -path '*/gradle-8*' -not -path '*/.gradle*' | sort >> "$OUTPUT"
echo "" >> "$OUTPUT"
echo "" >> "$OUTPUT"

echo "=====================================" >> "$OUTPUT"
echo "## ARQUIVOS BINÁRIOS (listagem)" >> "$OUTPUT"
echo "=====================================" >> "$OUTPUT"
echo "" >> "$OUTPUT"
find . -type f \( -name "*.jar" -o -name "*.class" -o -name "*.png" -o -name "*.ogg" \) | sort >> "$OUTPUT"
echo "" >> "$OUTPUT"
echo "" >> "$OUTPUT"

echo "=====================================" >> "$OUTPUT"
echo "## ARQUIVO: build.gradle" >> "$OUTPUT"
echo "=====================================" >> "$OUTPUT"
echo "" >> "$OUTPUT"
cat build.gradle >> "$OUTPUT"
echo "" >> "$OUTPUT"
echo "" >> "$OUTPUT"

echo "=====================================" >> "$OUTPUT"
echo "## ARQUIVO: settings.gradle" >> "$OUTPUT"
echo "=====================================" >> "$OUTPUT"
echo "" >> "$OUTPUT"
cat settings.gradle >> "$OUTPUT"
echo "" >> "$OUTPUT"
echo "" >> "$OUTPUT"

echo "=====================================" >> "$OUTPUT"
echo "## ARQUIVO: gradle.properties" >> "$OUTPUT"
echo "=====================================" >> "$OUTPUT"
echo "" >> "$OUTPUT"
cat gradle.properties >> "$OUTPUT"
echo "" >> "$OUTPUT"
echo "" >> "$OUTPUT"

for javafile in $(find src/main/java -name "*.java" | sort); do
  echo "=====================================" >> "$OUTPUT"
  echo "## ARQUIVO: $javafile" >> "$OUTPUT"
  echo "=====================================" >> "$OUTPUT"
  echo "" >> "$OUTPUT"
  cat "$javafile" >> "$OUTPUT"
  echo "" >> "$OUTPUT"
  echo "" >> "$OUTPUT"
done

for jsonfile in $(find src/main/resources -name "*.json" | sort); do
  echo "=====================================" >> "$OUTPUT"
  echo "## ARQUIVO: $jsonfile" >> "$OUTPUT"
  echo "=====================================" >> "$OUTPUT"
  echo "" >> "$OUTPUT"
  cat "$jsonfile" >> "$OUTPUT"
  echo "" >> "$OUTPUT"
  echo "" >> "$OUTPUT"
done

for tomlfile in $(find src/main/resources -name "*.toml" | sort); do
  echo "=====================================" >> "$OUTPUT"
  echo "## ARQUIVO: $tomlfile" >> "$OUTPUT"
  echo "=====================================" >> "$OUTPUT"
  echo "" >> "$OUTPUT"
  cat "$tomlfile" >> "$OUTPUT"
  echo "" >> "$OUTPUT"
  echo "" >> "$OUTPUT"
done

echo "✓ Relatório gerado em: $OUTPUT"
