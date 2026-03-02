#!/usr/bin/env python3
from PIL import Image
from collections import deque
import os

SRC = 'src/main/resources/assets/dragonblockinfinity/textures/gui/icons_btn.png'
DEST_DIR = 'src/main/resources/assets/dragonblockinfinity/textures/gui/buttons'

def ensure_dir(path):
    os.makedirs(path, exist_ok=True)

def load_image(path):
    return Image.open(path).convert('RGBA')

def find_components(img):
    w, h = img.size
    pixels = img.load()
    visited = [[False]*h for _ in range(w)]
    comps = []
    for x in range(w):
        for y in range(h):
            if visited[x][y]:
                continue
            r,g,b,a = pixels[x,y]
            if a == 0:
                visited[x][y] = True
                continue
            # BFS
            q = deque()
            q.append((x,y))
            visited[x][y] = True
            minx, miny, maxx, maxy = x, y, x, y
            while q:
                cx, cy = q.popleft()
                for dx,dy in ((1,0),(-1,0),(0,1),(0,-1)):
                    nx, ny = cx+dx, cy+dy
                    if 0 <= nx < w and 0 <= ny < h and not visited[nx][ny]:
                        rr,gg,bb,aa = pixels[nx,ny]
                        if aa != 0:
                            visited[nx][ny] = True
                            q.append((nx,ny))
                            if nx < minx: minx = nx
                            if ny < miny: miny = ny
                            if nx > maxx: maxx = nx
                            if ny > maxy: maxy = ny
                        else:
                            visited[nx][ny] = True
            comps.append((minx, miny, maxx+1, maxy+1))
    return comps

def save_components(img, comps, dest):
    ensure_dir(dest)
    comps_sorted = sorted(comps, key=lambda b: (b[1], b[0]))
    out_files = []
    for i, (l,t,r,b) in enumerate(comps_sorted, start=1):
        crop = img.crop((l,t,r,b))
        # trim possible empty rows/cols inside bbox (safety)
        bbox = crop.getbbox()
        if bbox:
            crop = crop.crop(bbox)
        name = f'button_{i:03d}_{l}_{t}_{r-l}x{b-t}.png'
        out_path = os.path.join(dest, name)
        crop.save(out_path)
        out_files.append(out_path)
    return out_files

def main():
    if not os.path.exists(SRC):
        print('Source image not found:', SRC)
        return
    img = load_image(SRC)
    comps = find_components(img)
    if not comps:
        print('No components found')
        return
    out = save_components(img, comps, DEST_DIR)
    print('Saved', len(out), 'buttons to', DEST_DIR)

if __name__ == '__main__':
    main()
