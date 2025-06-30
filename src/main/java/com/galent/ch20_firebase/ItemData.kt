package com.galent.ch20_firebase

data class ItemData(
    var email: String? = null,
    var date: String? = null,
    var content: String? = null,
    var docId: String = "" //  문서 삭제용으로 필요
)
