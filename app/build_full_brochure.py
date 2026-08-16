import json
import re
import os

files = [
    "app/src/main/assets/raw_part1.txt",
    "app/src/main/assets/raw_part2.txt",
    "app/src/main/assets/raw_part3.txt",
    "app/src/main/assets/raw_part4.txt"
]

all_companies = {}

current_company = "ميديكو"

known_companies = [
    "هابي كيور", "ميديكو", "بركات", "دومنا", "المتحدة", "لاما", "ابن رشد",
    "حياة فارما", "افاميا", "يونيفارما", "الرازي", "ابن الهيثم", "الفا",
    "فارما لاند", "روي فيت", "زين للمعقمات", "اميسا", "كيمي", "اوبري",
    "راشا", "اسيا", "السورية", "شفا", "ابن حيان", "ميديوتيك", "اوغاريت",
    "ماجيكو", "راما فارما", "السعد", "الفارس", "بحري", "بيوميد", "ابن زهر"
]

def clean_text(t):
    return t.replace("##", "").strip()

for fpath in files:
    if not os.path.exists(fpath):
        continue
    with open(fpath, "r", encoding="utf-8") as f:
        content = f.read()
    
    lines = content.strip().split("\n")
    for line in lines:
        line = line.strip()
        if not line:
            continue
        c_line = clean_text(line)
        
        # Check if line is company name alone
        found_alone = False
        for kc in known_companies:
            if c_line == kc or c_line == kc.replace(" ", ""):
                current_company = kc
                found_alone = True
                break
        if found_alone:
            continue
            
        # Parse price at the end
        price_match = re.search(r'(\d+)\s*$', c_line)
        if not price_match:
            continue
            
        price = float(price_match.group(1))
        text_before_price = c_line[:price_match.start()].strip()
        
        # Extract bonus
        bonus = "بدون"
        bonus_match = re.search(r'(\d+\+\d+)', text_before_price)
        if bonus_match:
            bonus = bonus_match.group(1)
            text_before_price = text_before_price.replace(bonus_match.group(0), "")
            
        text_before_price = text_before_price.replace("بونص", "").replace("يونص", "").strip()
        
        # Extract company from line if present
        for kc in known_companies:
            if kc in text_before_price:
                current_company = kc
                text_before_price = text_before_price.replace(kc, "").strip()
                break
            elif kc == "زين للمعقمات" and "زين" in text_before_price:
                current_company = "زين للمعقمات"
                text_before_price = text_before_price.replace("زين", "").strip()
                break
                
        drug_name = text_before_price.strip().lstrip("/").strip()
        if not drug_name:
            continue
            
        if current_company not in all_companies:
            all_companies[current_company] = {"عدد_الأصناف": 0, "الأدوية": []}
            
        drug_num = len(all_companies[current_company]["الأدوية"]) + 1
        all_companies[current_company]["الأدوية"].append({
            "رقم": drug_num,
            "اسم_الدواء": drug_name,
            "السعر_الصافي": price,
            "البونص": bonus
        })
        all_companies[current_company]["عدد_الأصناف"] = drug_num

result = {
    "معلومات_التواصل": {
        "الاسم": "فواز البشير",
        "الهاتف": "0995711536",
        "ملاحظة": "الطلب حصراً عن طريق المندوب"
    },
    "الشركات": all_companies,
    "إجمالي_الأصناف": sum(len(c["الأدوية"]) for c in all_companies.values()),
    "عدد_الشركات": len(all_companies)
}

output_path = "app/src/main/assets/brochure_medications.json"
with open(output_path, "w", encoding="utf-8") as f:
    json.dump(result, f, ensure_ascii=False, indent=2)

print(f"Generated {output_path} with {result['إجمالي_الأصناف']} items across {result['عدد_الشركات']} companies!")
