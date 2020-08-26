package com.example.workout.main.Singletons

class TrainingSingleton {
    var names: Array<String?> = arrayOfNulls(200)
    var counts: Array<Int?> = arrayOfNulls(200)
    var keys: Array<String?> = arrayOfNulls(200)

    companion object{
        private var instance = TrainingSingleton()

        fun getInstance(): TrainingSingleton?{
            if(instance == null){
                instance =
                    TrainingSingleton()
                instance.names = arrayOfNulls(200)
                instance.counts = arrayOfNulls(200)
                instance.keys = arrayOfNulls(200)
            }
            return instance
        }
    }
}