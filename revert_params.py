# Script untuk fix parameter names yang salah diganti
import os
import re

akademik_dir = r"c:\Users\Nurul Aini\SEMESTER 5\PTB\BismillahArsisi\ArsiSI_frontend\app\src\main\java\com\example\arsisi_frontend\ui\akademik"

# Mapping yang perlu di-revert (function parameter names)
revert_replacements = {
    r'judul\s*=\s*\{': 'title = {',  # TopAppBar title parameter
    r'judul\s*=\s*"': 'title = "',   # String parameter
    r'kategori:\s*String': 'category: String',  # Function signature
    r'deskripsi:\s*String': 'description: String',  # Function signature
    r'tanggal:\s*String': 'date: String',  # Function signature
    r'fun\s+HeaderImageSection\(category:': 'fun HeaderImageSection(kategori:',  # Specific function
    r'fun\s+MainInfoSection\(title:': 'fun MainInfoSection(judul:',  # Specific function
    r'fun\s+DescriptionSection\(description:': 'fun DescriptionSection(deskripsi:',  # Specific function
    r'fun\s+SectionTitle\(icon:\s*ImageVector,\s*title:': 'fun SectionTitle(icon: ImageVector, judul:',  # Specific function
}

for filename in os.listdir(akademik_dir):
    if filename.endswith('.kt'):
        filepath = os.path.join(akademik_dir, filename)
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()
        
        original_content = content
        for old, new in revert_replacements.items():
            content = re.sub(old, new, content)
        
        if content != original_content:
            with open(filepath, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"Reverted: {filename}")

print("Done!")
