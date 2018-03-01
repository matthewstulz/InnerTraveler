package com.github.stulzm2.innertraveler

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class InnerTraveler {

    var title: String? = null
    var desc: String? = null
    var imageUrl: String? = null
    var username: String? = null

    constructor(title: String, desc: String, imageUrl: String, username: String) {
        this.title = title
        this.desc = desc
        this.imageUrl = imageUrl
        this.username = username
    }

    constructor() {}

}