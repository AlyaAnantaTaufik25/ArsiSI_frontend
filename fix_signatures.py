# Final comprehensive fix for AkademikDetailScreen
import re

filepath = r"c:\Users\Nurul Aini\SEMESTER 5\PTB\BismillahArsisi\ArsiSI_frontend\app\src\main\java\com\example\arsisi_frontend\ui\akademik\AkademikDetailScreen.kt"

with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

# Fix function signatures to use standard English names (matching composable conventions)
content = re.sub(r'fun MainInfoSection\(judul: String, date: String, updatedAt: String\? = null\)', 
                 'fun MainInfoSection(title: String, date: String, updatedAt: String? = null)', content)

content = re.sub(r'fun SectionTitle\(icon: ImageVector, judul: String\)', 
                 'fun SectionTitle(icon: ImageVector, title: String)', content)

content = re.sub(r'fun DescriptionSection\(deskripsi: String\)', 
                 'fun DescriptionSection(description: String)', content)

content = re.sub(r'fun HeaderImageSection\(kategori: String\)', 
                 'fun HeaderImageSection(category: String)', content)

# Fix function calls to match new signatures
content = re.sub(r'judul = currentDoc\.judul', 'title = currentDoc.judul', content)
content = re.sub(r'kategori = currentDoc\.kategori', 'category = currentDoc.kategori', content)
content = re.sub(r'deskripsi = currentDoc\.deskripsi', 'description = currentDoc.deskripsi', content)

# Fix SectionTitle calls
content = re.sub(r'SectionTitle\(icon = ([^,]+), title = "([^"]+)"\)', 
                 r'SectionTitle(icon = \1, title = "\2")', content)

# Fix inside function bodies - use parameter names correctly
content = re.sub(r'(\s+)title,(\s+style)', r'\1title,\2', content)  # Keep title variable
content = re.sub(r'text = description,', 'text = description,', content)  # Keep description variable

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)

print("Fixed AkademikDetailScreen function signatures and calls")
