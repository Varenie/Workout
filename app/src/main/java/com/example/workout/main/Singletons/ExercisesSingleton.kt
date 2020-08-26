package com.example.workout.main.Singletons

class ExercisesSingleton {
    var names: Array<String?> = arrayOfNulls(200)
    var counts: Array<Int?> = arrayOfNulls(200)
    var keys: Array<String?> = arrayOfNulls(200)

    companion object{
        private var instance = ExercisesSingleton()

        fun getInstance(): ExercisesSingleton?{
            if(instance == null){
                instance =
                    ExercisesSingleton()
                instance.names = arrayOfNulls(200)
                instance.counts = arrayOfNulls(200)
                instance.keys = arrayOfNulls(200)
            }
            return instance
        }
    }
}