package com.atasoyyazilim.kurankerim.vakit

import androidx.annotation.StringRes
import com.atasoyyazilim.kurankerim.R

/** `sesDosyasi`: assets/ses/ezan/<ad>.m4a — Güneş vaktinde ezan okunmaz. */
enum class Vakit(@StringRes val adRes: Int, val sesDosyasi: String?) {
    IMSAK(R.string.vakit_imsak, "sabah"),
    GUNES(R.string.vakit_gunes, null),
    OGLE(R.string.vakit_ogle, "ogle"),
    IKINDI(R.string.vakit_ikindi, "ikindi"),
    AKSAM(R.string.vakit_aksam, "aksam"),
    YATSI(R.string.vakit_yatsi, "yatsi")
}
