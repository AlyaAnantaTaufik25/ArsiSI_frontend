# FINAL FIX - Revert to snake_case to match data model
import re

files_to_fix = [
    r"c:\Users\Nurul Aini\SEMESTER 5\PTB\BismillahArsisi\ArsiSI_frontend\app\src\main\java\com\example\arsisi_frontend\ui\akademik\AkademikDetailScreen.kt",
    r"c:\Users\Nurul Aini\SEMESTER 5\PTB\BismillahArsisi\ArsiSI_frontend\app\src\main\java\com\example\arsisi_frontend\ui\akademik\AkademikFormScreen.kt",
]

for filepath in files_to_fix:
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Revert camelCase back to snake_case for data model properties
    content = content.replace('currentDoc.fileUri', 'currentDoc.file_path')
    content = content.replace('attachment.fileUri', 'attachment.file_path')
    content = content.replace('fileName =', 'fileName =')  # Keep parameter names as is
    content = content.replace('fileSize =', 'fileSize =')  # Keep parameter names as is
    
    # But when accessing from data model, use snake_case
    content = re.sub(r'currentDoc\.file_name', 'currentDoc.file_name', content)
    content = re.sub(r'currentDoc\.file_size', 'currentDoc.file_size', content)
    content = re.sub(r'attachment\.file_name', 'attachment.file_name', content)
    content = re.sub(r'attachment\.file_size', 'attachment.file_size', content)
    
    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(content)
    
    print(f"Fixed {filepath.split('\\\\')[-1]}")

# Fix ViewModel
vm_path = r"c:\Users\Nurul Aini\SEMESTER 5\PTB\BismillahArsisi\ArsiSI_frontend\app\src\main\java\com\example\arsisi_frontend\ui\akademik\AkademikViewModel.kt"
with open(vm_path, 'r', encoding='utf-8') as f:
    content = f.read()

# ViewModel should use snake_case when creating AkademikDocument
content = re.sub(r'kategori\s*=\s*category', 'kategori = kategori', content)
content = re.sub(r'deskripsi\s*=\s*description', 'deskripsi = deskripsi', content)

with open(vm_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("Fixed AkademikViewModel.kt")
print("\nAll files fixed to use snake_case for data model properties!")
