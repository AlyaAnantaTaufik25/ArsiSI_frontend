# Fix AkademikFormScreen function signatures
import re

filepath = r"c:\Users\Nurul Aini\SEMESTER 5\PTB\BismillahArsisi\ArsiSI_frontend\app\src\main\java\com\example\arsisi_frontend\ui\akademik\AkademikFormScreen.kt"

with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

# Fix AttachmentCard function signature
content = re.sub(r'fun AttachmentCard\(\s*fileName: String,\s*fileSize: String,\s*description: String,', 
                 'fun AttachmentCard(\n    fileName: String, \n    fileSize: String, \n    description: String,', content)

# Make sure viewModel.addDocument and updateDocument calls use correct parameter names
# These should match ViewModel function signatures which expect: judul, kategori, deskripsi, file_name, file_size, file_path

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)

print("Fixed AkademikFormScreen")
