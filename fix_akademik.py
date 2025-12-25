# Script untuk fix property names di semua file Akademik
import os
import re

# Mapping property lama -> baru
replacements = {
    r'\.title\b': '.judul',
    r'\.category\b': '.kategori',
    r'\.description\b': '.deskripsi',
    r'\.date\b': '.tanggal',
    r'\.fileName\b': '.file_name',
    r'\.fileSize\b': '.file_size',
    r'\.fileUri\b': '.file_path',
    r'\.id\b': '.dokumen_id',
    r'\.updatedAt\b': '.updated_at',
    r'\btitle\s*=': 'judul =',
    r'\bcategory\s*=': 'kategori =',
    r'\bdescription\s*=': 'deskripsi =',
    r'\bdate\s*=': 'tanggal =',
    r'\bfileName\s*=': 'file_name =',
    r'\bfileSize\s*=': 'file_size =',
    r'\bfileUri\s*=': 'file_path =',
    r'\bupdatedAt\s*=': 'updated_at =',
    r'documentId:\s*String': 'documentId: Int',
    r'docId:\s*String': 'docId: Int',
}

akademik_dir = r"c:\Users\Nurul Aini\SEMESTER 5\PTB\BismillahArsisi\ArsiSI_frontend\app\src\main\java\com\example\arsisi_frontend\ui\akademik"

for filename in os.listdir(akademik_dir):
    if filename.endswith('.kt'):
        filepath = os.path.join(akademik_dir, filename)
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()
        
        original_content = content
        for old, new in replacements.items():
            content = re.sub(old, new, content)
        
        if content != original_content:
            with open(filepath, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"Updated: {filename}")

print("Done!")
