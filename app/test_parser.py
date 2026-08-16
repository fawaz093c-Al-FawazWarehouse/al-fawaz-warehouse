import json
import re

raw_file = "app/src/main/assets/raw_part1.txt"
with open(raw_file, "r", encoding="utf-8") as f:
    text = f.read()

lines = text.strip().split("\n")
data = {"الشركات": {}}

current_company = "ميديكو"

for line in lines:
    line = line.strip()
    if not line:
        continue
    if line.startswith("##"):
        clean_line = line[2:].strip()
    else:
        clean_line = line.strip()

    # check if line is just company name
    if clean_line in ["ميديكو", "هابي كيور", "بركات", "دومنا", "المتحدة", "ابن رشد", "ابن الهيثم", "ابن حيان", "ابن زهر", "اسيا", "افاميا", "الرازي", "السعد", "السورية", "الفا", "اميسا", "اوبري", "اوغاريت", "بحري", "بيوميد", "حياة فارما", "راشا", "راما فارما", "روي فيت", "زين للمعقمات", "شفا", "فارما لاند", "كيمي", "لاما", "ماجيكو", "مسعود", "ميديوتيك", "يونيفارما"]:
        current_company = clean_line
        continue

    # Extract price at end
    price_match = re.search(r'(\d+)\s*$', clean_line)
    if price_match:
        price = float(price_match.group(1))
        content = clean_line[:price_match.start()].strip()
        
        bonus = "بدون"
        bonus_match = re.search(r'(\d+\+\d+)', content)
        if bonus_match:
            bonus = bonus_match.group(1)
            content = content.replace(bonus_match.group(0), "").replace("بونص", "").replace("/", "").strip()
        
        # company in content?
        for comp in ["ميديكو", "هابي كيور", "بركات", "دومنا", "المتحدة", "ابن رشد", "ابن الهيثم", "ابن حيان", "ابن زهر", "اسيا", "افاميا", "الرازي", "السعد", "السورية", "الفا", "اميسا", "اوبري", "اوغاريت", "بحري", "بيوميد", "حياة فارما", "راشا", "راما فارما", "روي فيت", "زين", "شفا", "فارما لاند", "كيمي", "لاما", "ماجيكو", "مسعود", "ميديوتيك", "يونيفارما"]:
            if comp in content:
                current_company = comp if comp != "زين" else "زين للمعقمات"
                content = content.replace(comp, "").strip()
                break

        drug_name = content.strip().lstrip("/").strip()
        if drug_name:
            if current_company not in data["الشركات"]:
                data["الشركات"][current_company] = {"الأدوية": []}
            
            data["الشركات"][current_company]["الأدوية"].append({
                "رقم": len(data["الشركات"][current_company]["الأدوية"]) + 1,
                "اسم_الدواء": drug_name,
                "السعر_الصافي": price,
                "البونص": bonus
            })

print(f"Total companies parsed: {len(data['الشركات'])}")
for comp, cdata in data["الشركات"].items():
    print(f"Company {comp}: {len(cdata['الأدوية'])} items")
