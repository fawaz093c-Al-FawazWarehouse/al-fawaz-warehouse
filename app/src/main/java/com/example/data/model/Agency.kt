package com.example.data.model

import com.example.R

data class Agency(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val description: String,
    val iconName: String,
    val logoRes: Int? = null
)

val EXCLUSIVE_AGENCIES = listOf(
    Agency("domna", "دومنا", "Domna", "أدوية ومستحضرات دومنا الحصرية", "meds", R.drawable.img_logo_domina_1786651010926),
    Agency("barakat", "بركات", "Barakat", "معامل بركات الدوائية", "meds", R.drawable.ic_logo_barakat),
    Agency("medico", "ميديكو", "Medico", "أدوية ميديكو المعتمدة", "meds", R.drawable.img_logo_medico_1786650995201),
    Agency("allied", "المتحدة", "Allied", "الشركة المتحدة للصناعات الدوائية", "meds", R.drawable.ic_logo_allied),
    Agency("ibn_rushd", "ابن رشد", "Ibn Rushd", "معامل ابن رشد الطبية", "meds", R.drawable.ic_logo_ibn_rushd),
    Agency("lama", "لاما", "Lama", "مستحضرات لاما الدوائية", "meds", R.drawable.img_logo_lama_1786651028351),
    Agency("happy_cure", "هابي كيور", "Happy Cure", "منتجات هابي كيور للرعاية الصحية", "meds", R.drawable.img_logo_happycure_1786651042583),
    Agency("celia", "حليب سيليا", "Celia Milk", "حليب سيليا المغذي للأطفال بأعلى المعايير", "milk", R.drawable.ic_logo_celia),
    Agency("ibn_al_haytham", "ابن الهيثم", "Ibn Al-Haytham", "مستحضرات ابن الهيثم الدوائية", "meds", null),
    Agency("hayat_pharma", "حياة فارما", "Hayat Pharma", "منتجات ومتممات حياة فارما", "meds", null),
    Agency("afamia", "افاميا", "Afamia", "منتجات ومستحضرات شركة افاميا", "meds", null),
    Agency("unipharma", "يونيفارما", "Unipharma", "صناعات يونيفارما الدوائية", "meds", R.drawable.ic_logo_unipharma),
    Agency("al_razi", "الرازي", "Al Razi", "معامل الرازي الطبية والدوائية", "meds", null),
    Agency("alpha", "الفا", "Alpha", "مستحضرات شركة الفا الدوائية", "meds", null),
    Agency("pharma_land", "فارما لاند", "Pharma Land", "أدوية ومنتجات فارما لاند", "meds", null),
    Agency("roy_fit", "روي فيت", "Roy Fit", "متممات وفيتامينات روي فيت", "meds", null),
    Agency("zein", "زين للمعقمات", "Zein", "مطهرات ومعقمات ومستحضرات زين", "meds", R.drawable.ic_logo_zein),
    Agency("emessa", "اميسا", "Emessa", "مستحضرات اميسا الطبية", "meds", null),
    Agency("chemi", "كيمي", "Chemi", "منتجات معامل كيمي للصناعات الدوائية", "meds", null),
    Agency("obari", "اوبري", "Obari", "صناعات اوبري الدوائية الرائدة", "meds", null),
    Agency("rasha", "راشا", "Rasha", "مستحضرات معامل راشا الطبية", "meds", null),
    Agency("asia", "اسيا", "Asia", "أدوية ومستحضرات اسيا الدوائية", "meds", null),
    Agency("syrian", "السورية", "Syrian", "منتجات الشركة السورية للصناعات الدوائية", "meds", null),
    Agency("shafa", "شفا", "Shafa", "مستحضرات شفا للصناعات الدوائية", "meds", null),
    Agency("ibn_hayyan", "ابن حيان", "Ibn Hayyan", "مستحضرات ابن حيان الدوائية", "meds", null),
    Agency("mediotec", "ميديوتيك", "Mediotec", "مستحضرات ميديوتيك الطبية", "meds", null),
    Agency("sugarit", "اوغاريت", "Ougarit", "أدوية ومستحضرات اوغاريت", "meds", null),
    Agency("magico", "ماجيكو", "Magico", "منتجات ماجيكو الدوائية", "meds", null),
    Agency("rama_pharma", "راما فارما", "Rama Pharma", "مستحضرات راما فارما الدوائية", "meds", null),
    Agency("al_saad", "السعد", "Al Saad", "أدوية ومنتجات السعد الدوائية", "meds", null),
    Agency("al_fares", "الفارس", "Al Fares", "منتجات شركة الفارس الدوائية", "meds", null),
    Agency("bahri", "بحري", "Bahri", "مستحضرات بحري الدوائية", "meds", null),
    Agency("biomed", "بيوميد", "Biomed", "متممات وأدوية بيوميد الدوائية", "meds", null),
    Agency("ibn_zuhr", "ابن زهر", "Ibn Zuhr", "مستحضرات ابن زهر الطبية والدوائية", "meds", null)
)

fun getAgencyLogo(companyName: String): Int? {
    val clean = companyName.trim()
    return when {
        clean.contains("ميديكو") -> R.drawable.img_logo_medico_1786650995201
        clean.contains("دومنا") -> R.drawable.img_logo_domina_1786651010926
        clean.contains("بركات") -> R.drawable.ic_logo_barakat
        clean.contains("المتحدة") -> R.drawable.ic_logo_allied
        clean.contains("لاما") -> R.drawable.img_logo_lama_1786651028351
        clean.contains("هابي كيور") -> R.drawable.img_logo_happycure_1786651042583
        clean.contains("سيليا") -> R.drawable.ic_logo_celia
        clean.contains("ابن رشد") -> R.drawable.ic_logo_ibn_rushd
        clean.contains("يونيفارما") -> R.drawable.ic_logo_unipharma
        clean.contains("زين") -> R.drawable.ic_logo_zein
        else -> null
    }
}


