#!/usr/bin/env python3
import sys
import zipfile
import tempfile
import os
import shutil
import time

WORKSPACE = os.path.abspath(os.path.join(os.path.dirname(__file__), '..'))
REPO_SRC = os.path.join(WORKSPACE, 'src')


def is_java(p):
    return p.endswith('.java')


def non_empty_code_lines(path):
    count = 0
    in_block = False
    try:
        with open(path, 'r', encoding='utf-8', errors='ignore') as f:
            for line in f:
                s = line.strip()
                if not s:
                    continue
                if in_block:
                    if '*/' in s:
                        in_block = False
                        # remove up to closing comment and continue
                        s = s.split('*/',1)[1].strip()
                        if not s:
                            continue
                    else:
                        continue
                if s.startswith('//'):
                    continue
                if s.startswith('/*'):
                    if '*/' in s:
                        # comment on one line
                        rest = s.split('*/',1)[1].strip()
                        if rest:
                            count += 1
                    else:
                        in_block = True
                    continue
                count += 1
    except Exception:
        # fallback to simple non-empty line count
        try:
            with open(path, 'r', encoding='utf-8', errors='ignore') as f:
                return sum(1 for l in f if l.strip())
        except Exception:
            return 0
    return count


def choose_better(src_path, zip_path):
    if is_java(src_path) and is_java(zip_path):
        a = non_empty_code_lines(src_path)
        b = non_empty_code_lines(zip_path)
        if b > a:
            return 'zip'
        else:
            return 'repo'
    else:
        a = os.path.getsize(src_path)
        b = os.path.getsize(zip_path)
        if b > a:
            return 'zip'
        else:
            return 'repo'


def main(zipfile_path):
    if not os.path.isfile(zipfile_path):
        print('Zip não encontrado:', zipfile_path)
        return 2
    tmp = tempfile.mkdtemp(prefix='src_zip_')
    print('Extraindo', zipfile_path, '->', tmp)
    try:
        with zipfile.ZipFile(zipfile_path, 'r') as z:
            z.extractall(tmp)
    except Exception as e:
        print('Erro ao extrair zip:', e)
        shutil.rmtree(tmp, ignore_errors=True)
        return 3

    # try to find a folder named 'src' inside tmp
    extracted_src = None
    for root, dirs, files in os.walk(tmp):
        for d in dirs:
            if d == 'src':
                extracted_src = os.path.join(root, d)
                break
        if extracted_src:
            break
    if not extracted_src:
        # if no 'src' folder, treat tmp as the root
        extracted_src = tmp
    print('Usando pasta extraída de comparação:', extracted_src)

    changed = []
    examined = 0
    for root, dirs, files in os.walk(extracted_src):
        for fname in files:
            zip_fpath = os.path.join(root, fname)
            rel = os.path.relpath(zip_fpath, extracted_src)
            repo_fpath = os.path.join(REPO_SRC, rel)
            if os.path.exists(repo_fpath) and os.path.isfile(repo_fpath):
                examined += 1
                try:
                    choice = choose_better(repo_fpath, zip_fpath)
                except Exception as e:
                    print('Erro comparando', repo_fpath, zip_fpath, e)
                    continue
                if choice == 'zip':
                    # compare content to avoid unnecessary overwrite
                    try:
                        with open(repo_fpath, 'rb') as f1, open(zip_fpath, 'rb') as f2:
                            if f1.read() == f2.read():
                                continue
                    except Exception:
                        pass
                    # Não criar backups automáticos (excluído por solicitação do usuário)
                    # ensure target directory exists
                    os.makedirs(os.path.dirname(repo_fpath), exist_ok=True)
                    shutil.copy2(zip_fpath, repo_fpath)
                    changed.append(rel)
                    print('Atualizado:', rel)
    print('\nResumo:')
    print('Arquivos examinados (com correspondência em repo):', examined)
    print('Arquivos atualizados:', len(changed))
    for c in changed:
        print('-', c)

    shutil.rmtree(tmp, ignore_errors=True)
    return 0


if __name__ == '__main__':
    if len(sys.argv) < 2:
        print('Uso: merge_src_from_zip.py <arquivo.zip>')
        sys.exit(1)
    z = sys.argv[1]
    sys.exit(main(z))
