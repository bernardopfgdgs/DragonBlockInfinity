from PIL import Image, ImageDraw
import os

# Criar pasta se não existir
os.makedirs("src/main/resources/assets/dragonblockinfinity/textures/gui", exist_ok=True)

def criar_barra_gradiente(largura, altura, cor1, cor2, arquivo):
    img = Image.new('RGBA', (largura, altura), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # Desenhar gradiente suave
    for y in range(altura):
        ratio = y / altura
        r = int(cor1[0] * (1 - ratio) + cor2[0] * ratio)
        g = int(cor1[1] * (1 - ratio) + cor2[1] * ratio)
        b = int(cor1[2] * (1 - ratio) + cor2[2] * ratio)
        draw.line([(0, y), (largura, y)], fill=(r, g, b, 220))
    
    # Borda arredondada preta
    for i in range(2):
        draw.rounded_rectangle([i, i, largura-1-i, altura-1-i], radius=5, outline=(0, 0, 0, 255-i*50), width=1)
    
    img.save(arquivo)
    print(f"✓ {arquivo}")

# KI - Azul brilhante
criar_barra_gradiente(100, 15, (50, 150, 255), (0, 80, 200), 
    "src/main/resources/assets/dragonblockinfinity/textures/gui/ki_neutral.png")

# STAMINA - Amarelo/Laranja
criar_barra_gradiente(100, 15, (255, 220, 100), (200, 150, 0), 
    "src/main/resources/assets/dragonblockinfinity/textures/gui/stamina_bar.png")

# VIDA - Vermelho intenso
criar_barra_gradiente(100, 15, (255, 100, 100), (180, 0, 0), 
    "src/main/resources/assets/dragonblockinfinity/textures/gui/vida_bar.png")

print("\n🎨 Texturas profissionais criadas!")
