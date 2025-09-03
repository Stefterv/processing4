package processing.utils.download
interface Downloadable {
    val required: Boolean
    val name: String
    val version: String
    val description: String

    fun isDownloaded(): Boolean

    companion object{
        private val registrationListeners = mutableListOf<(Class<out Downloadable>) -> Unit>()
        private val registeredClasses = mutableSetOf<Class<out Downloadable>>()

        @JvmStatic
        fun addRegistrationListener(listener: (Class<out Downloadable>) -> Unit) {
            registrationListeners.add(listener)
        }

        @JvmStatic
        fun register(downloadableClass: Class<out Downloadable>) {
            if (registeredClasses.contains(downloadableClass)) return
            registeredClasses.add(downloadableClass)
            registrationListeners.forEach { it(downloadableClass) }
        }
    }
}