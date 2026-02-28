#!/usr/bin/env python3
import os
import json
from PIL import Image, ImageDraw

os.makedirs("src/main/resources/assets/dragonblockinfinity/textures/race", exist_ok=True)
os.makedirs("src/main/resources/assets/dragonblockinfinity/textures/entity", exist_ok=True)
os.makedirs("src/main/resources/assets/dragonblockinfinity/textures/items", exist_ok=True)
os.makedirs("src/main/resources/assets/dragonblockinfinity/lang", exist_ok=True)
os.makedirs("src/main/resources/assets/dragonblockinfinity/models/item", exist_ok=True)

print("🎨 Gerando texturas automaticamente...")

# RAÇAS
races = {
    "saiyan": (255, 220, 177),
    "namekian": (100, 200, 100),
    "arcosian": (255, 255, 255),
    "human": (255, 200, 160),
    "majin": (255, 150, 200)
}

for race_name, color in races.items():
    img = Image.new('RGB', (64, 64), color)
    draw = ImageDraw.Draw(img)
    draw.rectangle([20, 20, 24, 24], fill=(0, 0, 0))
    draw.rectangle([40, 20, 44, 24], fill=(0, 0, 0))
    draw.rectangle([28, 35, 36, 37], fill=(50, 50, 50))
    img.save(f"src/main/resources/assets/dragonblockinfinity/textures/race/{race_name}.png")
    print(f"✓ {race_name}.png")

# ITENS
items = {
    "ki_crystal": (100, 200, 255),
    "senzu_bean": (100, 200, 50),
    "dragon_radar": (255, 200, 0),
    "weighted_clothing": (80, 80, 80),
    "gravity_chamber": (150, 150, 150)
}

for item_name, color in items.items():
    img = Image.new('RGB', (16, 16), color)
    draw = ImageDraw.Draw(img)
    draw.rectangle([0, 0, 15, 15], outline=(0, 0, 0), width=1)
    img.save(f"src/main/resources/assets/dragonblockinfinity/textures/items/{item_name}.png")
    print(f"✓ {item_name}.png")

# AURAS
auras = {
    "aura_white": (255, 255, 255, 180),
    "aura_golden": (255, 215, 0, 180),
    "aura_blue": (100, 150, 255, 180),
    "aura_red": (255, 50, 50, 180),
    "aura_pink": (255, 100, 200, 180)
}

for aura_name, color in auras.items():
    img = Image.new('RGBA', (32, 64), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    draw.ellipse([5, 10, 27, 54], fill=color)
    img.save(f"src/main/resources/assets/dragonblockinfinity/textures/entity/{aura_name}.png")
    print(f"✓ {aura_name}.png")

# CABELO SSJ
hair = Image.new('RGBA', (64, 64), (0, 0, 0, 0))
draw = ImageDraw.Draw(hair)
draw.polygon([(32, 5), (28, 15), (36, 15)], fill=(255, 215, 0))
draw.polygon([(25, 10), (20, 20), (30, 20)], fill=(255, 215, 0))
draw.polygon([(39, 10), (34, 20), (44, 20)], fill=(255, 215, 0))
hair.save("src/main/resources/assets/dragonblockinfinity/textures/entity/hair_ssj.png")
print("✓ hair_ssj.png")

print("\n📝 JSONs...")

# IDIOMA
lang = {
    "item.dragonblockinfinity.ki_crystal": "Cristal de Ki",
    "item.dragonblockinfinity.senzu_bean": "Feijão Senzu",
    "race.dragonblockinfinity.saiyan": "Saiyajin",
    "race.dragonblockinfinity.arcosian": "Arcosiano",
    "gui.dragonblockinfinity.ki": "Ki"
}

with open("src/main/resources/assets/dragonblockinfinity/lang/pt_br.json", 'w', encoding='utf-8') as f:
    json.dump(lang, f, indent=2, ensure_ascii=False)
print("✓ pt_br.json")

# MODELOS
for item in items.keys():
    model = {"parent": "item/generated", "textures": {"layer0": f"dragonblockinfinity:items/{item}"}}
    with open(f"src/main/resources/assets/dragonblockinfinity/models/item/{item}.json", 'w') as f:
        json.dump(model, f, indent=2)
    print(f"✓ {item}.json")

print(f"\n🎉 PRONTO! {len(races)} raças | {len(items)} itens | {len(auras)} auras")
