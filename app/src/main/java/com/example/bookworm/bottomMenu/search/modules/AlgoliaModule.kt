package com.example.bookworm.bottomMenu.search.modules

import com.algolia.search.saas.Client
import kotlinx.coroutines.*
import org.json.JSONObject

class AlgoliaModule : CoroutineScope {

    override val coroutineContext = Job()
    val client = Client("OA5097NS7Y", "10009a947514e0b1e48c53b95767acd9")
    var index = client.initIndex("MyIdx")

    fun search() {
        launch(Dispatchers.Main) {
            val response = withContext(Dispatchers.Default) { //index.search()
                // }

            }
        }
    }

    fun Input(){
        // algolia는 데이터 넣을때 JSON형식임
        index.addObjectAsync(
            JSONObject().put("이름", "밸류")
                .put("책 이름", "토지"),
            null
        )
        index.addObjectAsync(
            JSONObject().put("이름", "밸류2")
                .put("책 이름", "이상한나라의앨리스"),
            null
        )
    }

}