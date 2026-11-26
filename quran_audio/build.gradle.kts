plugins {
    id("com.android.asset-pack")
}

assetPack {
    packName.set("quran_audio")
    dynamicDelivery {
        // Uygulamayla birlikte Google Play'den yüklenir; uygulama kendisi hiçbir şey indirmez.
        deliveryType.set("install-time")
    }
}
