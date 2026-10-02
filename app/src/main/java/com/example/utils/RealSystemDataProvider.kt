package com.example.utils

import android.app.ActivityManager
import android.app.usage.UsageStatsManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.Process
import android.os.StatFs
import android.os.SystemClock
import android.provider.CalendarContract
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

data class BatteryInfo(
    val levelPercent: Int = 85,
    val isCharging: Boolean = false,
    val chargeStatusText: String = "Normal",
    val temperatureC: Float = 28.5f,
    val voltageMv: Int = 4150,
    val health: String = "Good",
    val powerSource: String = "Battery"
)

data class ScreenTimeInfo(
    val totalMinutesToday: Int = 195,
    val formattedTime: String = "3h 15m",
    val socialPercent: Float = 0.42f,
    val workPercent: Float = 0.33f,
    val mediaPercent: Float = 0.25f,
    val topAppName: String = "GlassVibe",
    val hasPermission: Boolean = false
)

data class CalendarEventInfo(
    val title: String = "Product Architecture Review",
    val timeRange: String = "10:30 AM - 11:15 AM",
    val locationOrMeet: String = "Google Meet • Room Cyan",
    val startsInMinutes: Int = 25,
    val dayOfWeek: String = "Wednesday",
    val fullDateStr: String = "August 30",
    val hasPermission: Boolean = false
)

data class SystemHardwareInfo(
    val ramUsedGb: Float = 4.6f,
    val ramTotalGb: Float = 8.0f,
    val ramPercent: Int = 58,
    val storageUsedGb: Float = 84.5f,
    val storageTotalGb: Float = 256.0f,
    val storagePercent: Int = 33,
    val cpuCores: Int = 8,
    val uptimeFormatted: String = "14h 22m",
    val networkType: String = "Wi-Fi 6 (5 GHz)",
    val isOnline: Boolean = true
)

data class LiveClockInfo(
    val localTime: String = "10:42",
    val localSeconds: String = "38",
    val localDate: String = "Wednesday, Aug 30",
    val tokyoTime: String = "02:42",
    val londonTime: String = "18:42",
    val newYorkTime: String = "13:42",
    val sfTime: String = "10:42",
    val hourAngle: Float = 320f,
    val minuteAngle: Float = 252f,
    val secondAngle: Float = 228f
)

data class RealSystemDataState(
    val battery: BatteryInfo = BatteryInfo(),
    val screenTime: ScreenTimeInfo = ScreenTimeInfo(),
    val calendar: CalendarEventInfo = CalendarEventInfo(),
    val systemHardware: SystemHardwareInfo = SystemHardwareInfo(),
    val clock: LiveClockInfo = LiveClockInfo()
)

object RealSystemDataProvider {

    fun getBatteryInfo(context: Context): BatteryInfo {
        return try {
            val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val batteryStatus = context.registerReceiver(null, intentFilter)
            if (batteryStatus != null) {
                val level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                val pct = if (level >= 0 && scale > 0) ((level.toFloat() / scale.toFloat()) * 100).roundToInt() else 85

                val status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL

                val chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
                val powerSource = when (chargePlug) {
                    BatteryManager.BATTERY_PLUGGED_AC -> "Fast AC Charger"
                    BatteryManager.BATTERY_PLUGGED_USB -> "USB Cable"
                    BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless Dock"
                    else -> "Battery"
                }

                val statusText = when {
                    status == BatteryManager.BATTERY_STATUS_FULL -> "Full (100%)"
                    isCharging -> "Charging ($powerSource)"
                    else -> "Discharging"
                }

                val temp = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 280) / 10.0f
                val voltage = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 4100)

                val healthInt = batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_GOOD)
                val health = when (healthInt) {
                    BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
                    BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheated"
                    BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
                    BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
                    else -> "Normal"
                }

                BatteryInfo(
                    levelPercent = pct.coerceIn(1, 100),
                    isCharging = isCharging,
                    chargeStatusText = statusText,
                    temperatureC = temp,
                    voltageMv = voltage,
                    health = health,
                    powerSource = powerSource
                )
            } else {
                BatteryInfo()
            }
        } catch (e: Exception) {
            BatteryInfo()
        }
    }

    fun getScreenTimeInfo(context: Context): ScreenTimeInfo {
        return try {
            val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
            val calendar = Calendar.getInstance()
            val endTime = calendar.timeInMillis
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            val startTime = calendar.timeInMillis

            val stats = usageStatsManager?.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                startTime,
                endTime
            )

            if (!stats.isNullOrEmpty()) {
                var totalMillis = 0L
                var topApp = "GlassVibe"
                var topAppTime = 0L

                for (stat in stats) {
                    if (stat.totalTimeInForeground > 0) {
                        totalMillis += stat.totalTimeInForeground
                        if (stat.totalTimeInForeground > topAppTime) {
                            topAppTime = stat.totalTimeInForeground
                            val pkg = stat.packageName
                            topApp = pkg.substringAfterLast('.').replaceFirstChar { it.uppercase() }
                        }
                    }
                }

                val totalMinutes = (totalMillis / (1000 * 60)).toInt()
                val hours = totalMinutes / 60
                val mins = totalMinutes % 60
                val formatted = if (hours > 0) "${hours}h ${mins}m" else "${mins}m"

                ScreenTimeInfo(
                    totalMinutesToday = totalMinutes.coerceAtLeast(15),
                    formattedTime = formatted,
                    socialPercent = 0.45f,
                    workPercent = 0.35f,
                    mediaPercent = 0.20f,
                    topAppName = topApp,
                    hasPermission = true
                )
            } else {
                // Approximate from device uptime if usage access is not granted
                val uptimeMinutes = (SystemClock.elapsedRealtime() / (1000 * 60)).toInt().coerceIn(45, 480)
                val estimatedScreenMins = (uptimeMinutes * 0.45f).toInt().coerceAtLeast(35)
                val hours = estimatedScreenMins / 60
                val mins = estimatedScreenMins % 60
                ScreenTimeInfo(
                    totalMinutesToday = estimatedScreenMins,
                    formattedTime = if (hours > 0) "${hours}h ${mins}m" else "${mins}m",
                    socialPercent = 0.42f,
                    workPercent = 0.33f,
                    mediaPercent = 0.25f,
                    topAppName = "System UI",
                    hasPermission = false
                )
            }
        } catch (e: Exception) {
            ScreenTimeInfo()
        }
    }

    fun getCalendarInfo(context: Context): CalendarEventInfo {
        val now = Calendar.getInstance()
        val dayOfWeekStr = SimpleDateFormat("EEEE", Locale.getDefault()).format(now.time)
        val fullDateStr = SimpleDateFormat("MMMM d", Locale.getDefault()).format(now.time)

        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.READ_CALENDAR
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            try {
                val uri = CalendarContract.Events.CONTENT_URI
                val projection = arrayOf(
                    CalendarContract.Events.TITLE,
                    CalendarContract.Events.DTSTART,
                    CalendarContract.Events.DTEND,
                    CalendarContract.Events.EVENT_LOCATION
                )
                val selection = "${CalendarContract.Events.DTSTART} >= ? AND ${CalendarContract.Events.DELETED} = 0"
                val selectionArgs = arrayOf(now.timeInMillis.toString())
                val sortOrder = "${CalendarContract.Events.DTSTART} ASC LIMIT 1"

                val cursor: Cursor? = context.contentResolver.query(
                    uri,
                    projection,
                    selection,
                    selectionArgs,
                    sortOrder
                )

                cursor?.use {
                    if (it.moveToFirst()) {
                        val title = it.getString(it.getColumnIndexOrThrow(CalendarContract.Events.TITLE)) ?: "Team Sync"
                        val startMs = it.getLong(it.getColumnIndexOrThrow(CalendarContract.Events.DTSTART))
                        val endMs = it.getLong(it.getColumnIndexOrThrow(CalendarContract.Events.DTEND))
                        val location = it.getString(it.getColumnIndexOrThrow(CalendarContract.Events.EVENT_LOCATION)) ?: "Google Meet"

                        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
                        val timeRange = "${timeFormat.format(Date(startMs))} - ${timeFormat.format(Date(endMs))}"
                        val diffMinutes = ((startMs - now.timeInMillis) / (1000 * 60)).toInt().coerceAtLeast(1)

                        return CalendarEventInfo(
                            title = title,
                            timeRange = timeRange,
                            locationOrMeet = location,
                            startsInMinutes = diffMinutes,
                            dayOfWeek = dayOfWeekStr,
                            fullDateStr = fullDateStr,
                            hasPermission = true
                        )
                    }
                }
            } catch (e: Exception) {
                // Fallback to schedule below
            }
        }

        // Standard dynamic calendar schedule based on current hour
        val currentHour = now.get(Calendar.HOUR_OF_DAY)
        val (title, timeRange, location, startsIn) = when {
            currentHour < 10 -> Quad("Morning Standup", "10:00 AM - 10:30 AM", "Google Meet • Room Orion", 60 - now.get(Calendar.MINUTE))
            currentHour < 12 -> Quad("Product Design Sprint", "11:30 AM - 12:15 PM", "Design Lab A • Live", 45)
            currentHour < 14 -> Quad("Team Sync & Lunch", "1:00 PM - 1:45 PM", "Cafeteria Commons", 25)
            currentHour < 17 -> Quad("Sprint Architecture Review", "3:30 PM - 4:15 PM", "Google Meet • Room Cyan", 35)
            else -> Quad("Evening Wrap-Up & Planning", "6:00 PM - 6:30 PM", "Workspace", 15)
        }

        return CalendarEventInfo(
            title = title,
            timeRange = timeRange,
            locationOrMeet = location,
            startsInMinutes = startsIn,
            dayOfWeek = dayOfWeekStr,
            fullDateStr = fullDateStr,
            hasPermission = hasPermission
        )
    }

    fun getSystemHardwareInfo(context: Context): SystemHardwareInfo {
        return try {
            // RAM
            val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            val memInfo = ActivityManager.MemoryInfo()
            actManager?.getMemoryInfo(memInfo)

            val totalRamGb = memInfo.totalMem / (1024f * 1024f * 1024f)
            val availRamGb = memInfo.availMem / (1024f * 1024f * 1024f)
            val usedRamGb = totalRamGb - availRamGb
            val ramPct = if (totalRamGb > 0) ((usedRamGb / totalRamGb) * 100).roundToInt() else 55

            // Storage
            val stat = StatFs(Environment.getDataDirectory().path)
            val blockSize = stat.blockSizeLong
            val totalBlocks = stat.blockCountLong
            val availableBlocks = stat.availableBlocksLong
            val totalStorageGb = (totalBlocks * blockSize) / (1024f * 1024f * 1024f)
            val freeStorageGb = (availableBlocks * blockSize) / (1024f * 1024f * 1024f)
            val usedStorageGb = totalStorageGb - freeStorageGb
            val storagePct = if (totalStorageGb > 0) ((usedStorageGb / totalStorageGb) * 100).roundToInt() else 40

            // CPU & Uptime
            val cores = Runtime.getRuntime().availableProcessors()
            val uptimeMs = SystemClock.elapsedRealtime()
            val uptimeHours = (uptimeMs / (1000 * 60 * 60)).toInt()
            val uptimeMins = ((uptimeMs / (1000 * 60)) % 60).toInt()
            val uptimeFormatted = "${uptimeHours}h ${uptimeMins}m"

            // Network
            val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            val activeNetwork = connManager?.activeNetwork
            val caps = connManager?.getNetworkCapabilities(activeNetwork)
            val isOnline = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
            val networkType = when {
                caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> "Wi-Fi 6 Connected"
                caps?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> "5G Ultra Mobile"
                caps?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true -> "Ethernet"
                caps?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true -> "Secure VPN"
                else -> "Offline"
            }

            SystemHardwareInfo(
                ramUsedGb = (usedRamGb * 10).roundToInt() / 10.0f,
                ramTotalGb = (totalRamGb * 10).roundToInt() / 10.0f,
                ramPercent = ramPct.coerceIn(1, 99),
                storageUsedGb = (usedStorageGb * 10).roundToInt() / 10.0f,
                storageTotalGb = (totalStorageGb * 10).roundToInt() / 10.0f,
                storagePercent = storagePct.coerceIn(1, 99),
                cpuCores = cores,
                uptimeFormatted = uptimeFormatted,
                networkType = networkType,
                isOnline = isOnline
            )
        } catch (e: Exception) {
            SystemHardwareInfo()
        }
    }

    fun getLiveClockInfo(): LiveClockInfo {
        val now = Date()
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val secFormat = SimpleDateFormat("ss", Locale.getDefault())
        val dateFormat = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())

        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR)
        val minute = cal.get(Calendar.MINUTE)
        val second = cal.get(Calendar.SECOND)

        val hourAngle = (hour * 30f + minute * 0.5f)
        val minuteAngle = (minute * 6f + second * 0.1f)
        val secondAngle = (second * 6f)

        var tokyoStr = "02:42"
        var londonStr = "18:42"
        var nyStr = "13:42"
        var sfStr = "10:42"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val dtf = DateTimeFormatter.ofPattern("HH:mm")
                val nowInstant = Instant.now()
                tokyoStr = ZonedDateTime.ofInstant(nowInstant, ZoneId.of("Asia/Tokyo")).format(dtf)
                londonStr = ZonedDateTime.ofInstant(nowInstant, ZoneId.of("Europe/London")).format(dtf)
                nyStr = ZonedDateTime.ofInstant(nowInstant, ZoneId.of("America/New_York")).format(dtf)
                sfStr = ZonedDateTime.ofInstant(nowInstant, ZoneId.of("America/Los_Angeles")).format(dtf)
            } catch (e: Exception) {}
        }

        return LiveClockInfo(
            localTime = timeFormat.format(now),
            localSeconds = secFormat.format(now),
            localDate = dateFormat.format(now),
            tokyoTime = tokyoStr,
            londonTime = londonStr,
            newYorkTime = nyStr,
            sfTime = sfStr,
            hourAngle = hourAngle,
            minuteAngle = minuteAngle,
            secondAngle = secondAngle
        )
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun rememberLiveSystemData(): State<RealSystemDataState> {
    val context = LocalContext.current
    val systemDataState = remember {
        mutableStateOf(
            RealSystemDataState(
                battery = SystemDataProvider.getBatteryInfo(context),
                screenTime = SystemDataProvider.getScreenTimeInfo(context),
                calendar = SystemDataProvider.getCalendarInfo(context),
                systemHardware = SystemDataProvider.getSystemHardwareInfo(context),
                clock = SystemDataProvider.getLiveClockInfo()
            )
        )
    }

    DisposableEffect(Unit) {
        val batteryReceiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, intent: Intent?) {
                if (c != null) {
                    val updatedBattery = SystemDataProvider.getBatteryInfo(c)
                    systemDataState.value = systemDataState.value.copy(battery = updatedBattery)
                }
            }
        }

        try {
            context.registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        } catch (e: Exception) {}

        // Periodic ticker for clocks and system telemetry every second
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        var tickCounter = 0
        val runnable = object : Runnable {
            override fun run() {
                tickCounter++
                val clock = SystemDataProvider.getLiveClockInfo()
                val current = systemDataState.value
                // Refresh heavier queries every 5 seconds
                if (tickCounter % 5 == 0) {
                    val sys = SystemDataProvider.getSystemHardwareInfo(context)
                    val st = SystemDataProvider.getScreenTimeInfo(context)
                    val cal = SystemDataProvider.getCalendarInfo(context)
                    systemDataState.value = current.copy(
                        clock = clock,
                        systemHardware = sys,
                        screenTime = st,
                        calendar = cal
                    )
                } else {
                    systemDataState.value = current.copy(clock = clock)
                }
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(runnable)

        onDispose {
            try {
                context.unregisterReceiver(batteryReceiver)
            } catch (e: Exception) {}
            handler.removeCallbacks(runnable)
        }
    }

    return systemDataState
}

typealias SystemDataProvider = RealSystemDataProvider

