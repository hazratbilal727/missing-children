package com.example.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.LayoutDirection

enum class AppLanguage(val code: String, val label: String, val layoutDirection: LayoutDirection) {
    ENGLISH("en", "English", LayoutDirection.Ltr),
    URDU("ur", "اردو (Urdu)", LayoutDirection.Rtl)
}

object AppStrings {
    private val en = mapOf(
        "app_title" to "Missing Children",
        "app_tagline" to "Child Protection & Recovery Portal Pakistan",
        "nav_home" to "Home",
        "nav_pending" to "Pending Cases",
        "nav_completed" to "Completed Cases",
        "nav_my_cases" to "My Cases",
        "nav_profile" to "Profile",
        
        "stats_verified" to "Total Verified",
        "stats_active" to "Active Cases",
        "stats_resolved" to "Successfully Resolved",
        "stats_recent" to "Recent (7 Days)",
        
        "report_missing_child" to "Report Missing Child",
        "emergency_alert_banner" to "Immediate Danger? Dial Zainab Alert 1121 or Police 15",
        "call_1121" to "Call 1121",
        "call_15" to "Call 15",
        
        "feed_title" to "Verified Missing Children Feed",
        "search_hint" to "Search by child name, city, province...",
        "all_provinces" to "All Provinces",
        
        "view_details" to "View Details",
        "i_have_info" to "I Have Information",
        "share_case" to "Share Case",
        "confidential_sighting" to "Submit Confidential Sighting",
        "sighting_submitted_success" to "Sighting submitted successfully and encrypted for case handlers.",
        
        "case_ref" to "Case Reference",
        "last_seen" to "Last Seen",
        "age" to "Age",
        "years_old" to "years old",
        "gender" to "Gender",
        "status" to "Status",
        "clothing" to "Clothing",
        "physical_traits" to "Physical Traits",
        "circumstances" to "Circumstances",
        
        "status_under_review" to "Under Review",
        "status_active" to "Active Public Case",
        "status_resolved" to "Resolved / Safe",
        "status_rejected" to "Clarification Required",
        
        "privacy_resolved_notice" to "To safeguard the child's long-term dignity and privacy, public identifying photos are archived and reunion addresses remain confidential.",
        "resolved_badge" to "Reunited / Case Resolved",
        "my_cases_title" to "My Submitted Reports",
        "my_cases_empty" to "You have not submitted any missing child reports yet.",
        "admin_portal" to "Moderation & Verification Portal",
        "admin_approve" to "Approve & Publish",
        "admin_reject" to "Reject / Request Proof",
        "admin_resolve" to "Confirm Resolution",
        "admin_sightings" to "Citizen Sightings",
        
        "language" to "Language",
        "switch_to_urdu" to "اردو میں دیکھیں",
        "switch_to_english" to "Switch to English",
        "current_role" to "Active Role",
        "safety_guide" to "Child Safety & Legal Rights (Zainab Alert Act)",
        "helplines" to "Emergency Helplines Directory",
        "privacy_policy" to "Privacy & Safeguarding Policy",
        "about_app" to "About Missing Children Pakistan"
    )

    private val ur = mapOf(
        "app_title" to "گمشدہ بچے",
        "app_tagline" to "چائلڈ پروٹیکشن اینڈ ریکوری پورٹل پاکستان",
        "nav_home" to "مرکزی صفحہ",
        "nav_pending" to "زیرِ تفتیش کیسز",
        "nav_completed" to "حل شدہ کیسز",
        "nav_my_cases" to "میری رپورٹس",
        "nav_profile" to "پروفائل",
        
        "stats_verified" to "کل تصدیق شدہ",
        "stats_active" to "موجودہ گمشدہ کیسز",
        "stats_resolved" to "کامیابی سے بازیاب",
        "stats_recent" to "حالیہ رپورٹس (7 دن)",
        
        "report_missing_child" to "گمشدہ بچے کی اطلاع دیں",
        "emergency_alert_banner" to "فوری خطرہ ہے؟ زینب الرٹ 1121 یا پولیس 15 پر رابطہ کریں",
        "call_1121" to "1121 پر کال کریں",
        "call_15" to "15 پر کال کریں",
        
        "feed_title" to "تصدیق شدہ گمشدہ بچوں کی فہرست",
        "search_hint" to "بچے کے نام، شہر یا صوبے سے تلاش کریں...",
        "all_provinces" to "تمام صوبے",
        
        "view_details" to "تفصیلات دیکھیں",
        "i_have_info" to "میرے پاس معلومات ہیں",
        "share_case" to "کیس شیئر کریں",
        "confidential_sighting" to "خفیہ اطلاع / موجودگی کی اطلاع",
        "sighting_submitted_success" to "اطلاع کامیابی سے موصول ہو گئی اور تفتیشی افسران کو بھیج دی گئی۔",
        
        "case_ref" to "کیس ریفرنس نمبر",
        "last_seen" to "آخری بار دیکھا گیا",
        "age" to "عمر",
        "years_old" to "سال",
        "gender" to "جنس",
        "status" to "حالت",
        "clothing" to "لباس",
        "physical_traits" to "ظاہری علامات",
        "circumstances" to "گمشدگی کے حالات",
        
        "status_under_review" to "زیرِ تصدیق",
        "status_active" to "فعال عوامی کیس",
        "status_resolved" to "بازیاب شدہ / محفوظ",
        "status_rejected" to "وضاحت درکار ہے",
        
        "privacy_resolved_notice" to "بچے کے تحفظ اور پرائیویسی کے پیشِ نظر بازیابی کے بعد شناختی تصاویر ہٹا دی گئی ہیں اور ذاتی معلومات خفیہ رکھی جاتی ہیں۔",
        "resolved_badge" to "بحفاظت بازیاب / کیس مکمل",
        "my_cases_title" to "میری درج کروائی گئی رپورٹس",
        "my_cases_empty" to "آپ نے ابھی تک کوئی گمشدگی کی رپورٹ درج نہیں کروائی۔",
        "admin_portal" to "انتظامی تصدیق و پورٹل",
        "admin_approve" to "تصدیق اور شائع کریں",
        "admin_reject" to "رد کریں / ثبوت مانگیں",
        "admin_resolve" to "کیس کی بازیابی تصدیق کریں",
        "admin_sightings" to "شہریوں کی اطلاعات",
        
        "language" to "زبان",
        "switch_to_urdu" to "اردو میں دیکھیں",
        "switch_to_english" to "English",
        "current_role" to "موجودہ کردار",
        "safety_guide" to "بچوں کا تحفظ اور قانونی حقوق (زینب الرٹ ایکٹ)",
        "helplines" to "ہنگامی ہیلپ لائنز ڈائرکٹری",
        "privacy_policy" to "پرائیویسی اور چائلڈ پروٹیکشن پالیسی",
        "about_app" to "ایپ کے بارے میں"
    )

    fun get(key: String, lang: AppLanguage): String {
        return when (lang) {
            AppLanguage.URDU -> ur[key] ?: en[key] ?: key
            AppLanguage.ENGLISH -> en[key] ?: key
        }
    }
}

val LocalAppLanguage = staticCompositionLocalOf { AppLanguage.ENGLISH }

@Composable
fun stringRes(key: String): String {
    val lang = LocalAppLanguage.current
    return AppStrings.get(key, lang)
}
