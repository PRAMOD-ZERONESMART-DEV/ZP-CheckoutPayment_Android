import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class Globals {

    companion object {
        var AUTH_TOKEN = ""
        var RETURN_URL = ""
        var isProdEnable = false;
        //var PUBLIC_KEY = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAIQfVHLl3zPA0M0iLefvWNawMfgxwJHRHqf/s0X/SI7brAxMjkEiA0ysv/maJ1bCHkYTqNwKeszqfaSdjYf6GI8CAwEAAQ=="

        fun generateRandom16DigitNumber(): Long {
            val upperBound = 999_999_999_999_999_9L // 16-digit upper bound (inclusive)
            val lowerBound = 100_000_000_000_000_0L // 16-digit lower bound (inclusive)

            return Random.nextLong(lowerBound, upperBound + 1)
        }

        fun generateUnique16DigitNumberUUID(): String {
            val uuid = UUID.randomUUID()
            return uuid.toString().replace("-", "").substring(0, 16)
        }

        fun generateUnique16DigitNumber(): String {
            val timestamp = SimpleDateFormat("yyMMddHHmmssSSS").format(Date())
            val randomPart = String.format("%04d", Random.nextInt(10000))
            return timestamp + randomPart
        }

        fun isEmailValid(email: String): Boolean {
            val emailRegex = Regex("^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,})+\$")
            return email.matches(emailRegex)
        }
    }


}

