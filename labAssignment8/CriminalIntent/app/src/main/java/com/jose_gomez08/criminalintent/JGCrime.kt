package com.jose_gomez08.criminalintent

import java.util.*
import java.util.Date

data class JGCrime( val jdID: UUID = UUID.randomUUID(),
                    var jgTitle: String = "",
                    var jgDate: Date = Date(),
                    var jgIsSolved: Boolean = false ){
}