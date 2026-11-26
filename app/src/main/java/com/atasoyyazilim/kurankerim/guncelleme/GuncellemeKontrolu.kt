package com.atasoyyazilim.kurankerim.guncelleme

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.atasoyyazilim.kurankerim.R
import com.atasoyyazilim.kurankerim.ui.theme.Manrope
import com.atasoyyazilim.kurankerim.ui.theme.Renk
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun GuncellemeKontrolu(content: @Composable (()->Unit)->Unit) {
    val c=LocalContext.current
    val depo=remember(c){GuncellemeDeposu(c)}
    val prefs=remember(c){c.getSharedPreferences("guncelleme",0)}
    val scope=rememberCoroutineScope()
    var acik by remember { mutableStateOf(false) }
    var mesaji by remember { mutableIntStateOf(R.string.guncelleme_araniyor) }
    var busy by remember { mutableStateOf(false) }
    var surum by remember { mutableStateOf<YeniSurum?>(null) }
    var apk by remember { mutableStateOf<File?>(null) }
    var progress by remember { mutableFloatStateOf(0f) }
    var downloading by remember { mutableStateOf(false) }
    fun kontrol(manuel:Boolean) {
        if(busy)return
        busy=true;surum=null;apk=null
        mesaji=R.string.guncelleme_araniyor
        if(manuel)acik=true
        scope.launch {
            try {
                val sonuc=depo.kontrol()
                prefs.edit().putLong("son_kontrol",System.currentTimeMillis()).apply()
                surum=sonuc
                mesaji=if(sonuc==null)R.string.guncelleme_yok else R.string.guncelleme_mevcut
                if(sonuc!=null)acik=true
            } catch(e:CancellationException){throw e}
            catch(_:Exception){mesaji=R.string.guncelleme_baglanti}
            finally {busy=false}
        }
    }
    LaunchedEffect(Unit) {
        val once=prefs.getLong("son_kontrol",0)
        if(System.currentTimeMillis()-once >= 86_400_000L || once>System.currentTimeMillis())kontrol(false)
    }
    content { kontrol(true) }
    if(acik) Dialog(onDismissRequest={if(!busy)acik=false}) {
        Column(Modifier.fillMaxWidth().background(Renk.YesilKoyu,RoundedCornerShape(22.dp)).padding(20.dp)) {
            Text(stringResource(R.string.guncelleme_baslik),fontFamily=Manrope,fontSize=20.sp,fontWeight=FontWeight.ExtraBold,color=Renk.Altin)
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.guncelleme_surumu,depo.kuruluSurum),fontFamily=Manrope,color=Renk.KoyuMetin)
            surum?.let { Text(stringResource(R.string.guncelleme_yeni,it.ad),fontFamily=Manrope,color=Renk.Beyaz) }
            Spacer(Modifier.height(8.dp))
            Text(stringResource(mesaji),fontFamily=Manrope,color=Renk.Beyaz)
            if(downloading) {
                Spacer(Modifier.height(12.dp))
                LinearProgressIndicator(progress={progress.coerceIn(0f,1f)},modifier=Modifier.fillMaxWidth(),color=Renk.Altin)
            }
            Spacer(Modifier.height(16.dp))
            val s=surum
            if(s!=null && !busy) Button(
                onClick={
                    val f=apk
                    if(f==null) {
                        busy=true;downloading=true;progress=0f;mesaji=R.string.guncelleme_indiriliyor
                        scope.launch {
                            try {
                                apk=depo.indir(s){progress=it}
                                mesaji=R.string.guncelleme_dogrulandi
                            } catch(e:CancellationException){throw e}
                            catch(_:Exception){apk=null;mesaji=R.string.guncelleme_red}
                            finally {busy=false;downloading=false}
                        }
                    } else if(!c.packageManager.canRequestPackageInstalls()) {
                        mesaji=R.string.guncelleme_izin
                        try {c.startActivity(Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,Uri.parse("package:"+c.packageName)))}
                        catch(_:Exception){mesaji=R.string.guncelleme_red}
                    } else {
                        busy=true
                        scope.launch {
                            try {c.startActivity(depo.kurulumNiyeti(f,s))}
                            catch(e:CancellationException){throw e}
                            catch(_:Exception){apk=null;mesaji=R.string.guncelleme_red}
                            finally {busy=false}
                        }
                    }
                },modifier=Modifier.fillMaxWidth(),
                colors=ButtonDefaults.buttonColors(containerColor=Renk.Altin,contentColor=Renk.YesilKoyu)
            ) { Text(stringResource(if(apk==null)R.string.guncelleme_indir else R.string.guncelleme_yukle),fontFamily=Manrope,fontWeight=FontWeight.ExtraBold) }
            if(!busy) TextButton(onClick={acik=false},modifier=Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.guncelleme_sonra),fontFamily=Manrope,color=Renk.KoyuMetin)
            }
        }
    }
}
