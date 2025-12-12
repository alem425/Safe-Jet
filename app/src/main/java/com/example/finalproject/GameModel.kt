package com.example.finalproject
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlin.math.abs

//game state enum
enum class GameState {
    PLAYING,
    PAUSED,
    GAME_OVER
}

//obstacle type enum
enum class ObstacleType {
    COIN,
    STAR,
    DANGER
}

//data class for obstacles
data class Obstacle(
    var x: Float,            // X position (0-1 relative to screen width)
    var y: Float,            // Y position (0-1 relative to screen height)
    val type: ObstacleType,  // Type of obstacle (coin, star, danger)
    val speed: Float = 0.01f  // How fast the obstacle moves down (per update)
)

//data class for plane
data class Plane(
    var x: Float = 0.5f,   // X position (0-1 relative to screen width)
    val y: Float = 0.8f,   // Y position (fixed near bottom)
    var type: String = "plane1",
    var lives: Int = 3,
    val width: Float = 0.20f,   // Size relative to screen
    val height: Float = 0.20f
)

//data class for score
data class Score(
    var coins: Int = 0,
    var stars: Int = 0,
    var timeAlive: Long = 0, //milliseconds
    var total: Int = 0,
    var speedMultiplier: Float = 1.0f
) {
    fun calculateTotal(): Int{
        // coin = 10 points, star = 100 points, time = 1 point per second
        //base values
        val coinScore = coins * 10
        val starScore = stars * 100
        val timeScore = (timeAlive / 1000).toInt()

        // Apply speed bonus (higher speed = more points)
        val speedBonus = when {
            speedMultiplier >= 1.8f -> 2.0f  // 2x points for very fast
            speedMultiplier >= 1.4f -> 1.5f  // 1.5x points for fast
            speedMultiplier <= 0.7f -> 0.7f  // 0.7x points for very slow
            speedMultiplier <= 0.9f -> 0.8f  // 0.8x points for slow
            else -> 1.0f                     // Normal speed = normal points
        }

        val baseTotal = coinScore + starScore + timeScore
        return (baseTotal * speedBonus).toInt()
//        return (coins * 10) + (stars * 100) + (timeAlive / 1000).toInt()
    }
}

class GameModel(context: Context): SensorEventListener {
    //GameModel handles...
        //plane movement (accelerometer-controlled)
        //obstacle generation (coins, stars, dangers)
        //score tracking (coins, stars, time)
        //collision detection
        //game state management

    //game state
    var gameState: GameState = GameState.PLAYING
    private var gameStartTime: Long = System.currentTimeMillis()

    val plane = Plane() // Plane
    val score = Score() // Score

    // Obstacles
    private val obstacles = mutableListOf<Obstacle>()
    private var lastObstacleTime: Long = 0
    private val obstacleInterval: Long = 1000  // Spawn every 1 second
    var obstacleSpeedMultiplier: Float = 1.0f //default normal speed

    // Screen dimensions (set from View)
    var screenWidth: Int = 1080
    var screenHeight: Int = 1920

    // Accelerometer control
    private val sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    // Sensitivity for accelerometer (adjustable through seek bar later)
    var tiltSensitivity: Float = 0.0085f

    // Game listeners for UI updates
    interface GameStateListener {
        fun onScoreUpdated()
        fun onLivesUpdated()
        fun onObstaclesUpdated()
        fun onGameOver(finalScore: Int)
    }
    private val listeners = mutableListOf<GameStateListener>()

    init {
        // Register accelerometer listener
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    // Update game state (called regularly like every 16ms for ~60fps)
    fun update(currentTime: Long) {
        if (gameState != GameState.PLAYING) return

        // Update time alive
        score.timeAlive = currentTime - gameStartTime

        // Pass speed multiplier to score
        score.speedMultiplier = obstacleSpeedMultiplier

        // Spawn new obstacles
        if (currentTime - lastObstacleTime > obstacleInterval) {
            spawnObstacle()
            lastObstacleTime = currentTime
        }

        // Move all obstacles down
        for (obstacle in obstacles) {
            obstacle.y += obstacle.speed
        }

        // Remove obstacles that went off screen
        obstacles.removeAll { it.y > 1.0f }

        // Check collisions
        checkCollisions()

        // Update total score
        score.total = score.calculateTotal()

        // Notify listeners
        notifyObstaclesUpdated()
    }

    private fun spawnObstacle() {
        // Random X position (0.1 to 0.9 to keep away from edges)
        val randomX = 0.1f + (Math.random().toFloat() * 0.8f)

        // Random type with probabilities
        val randomValue = Math.random()
        val type = when {
            randomValue < 0.6 -> ObstacleType.COIN     // 60% chance
            randomValue < 0.8 -> ObstacleType.STAR     // 20% chance
            else -> ObstacleType.DANGER               // 20% chance
        }

        // Different speeds for different types
        val baseSpeed = when (type) {
            ObstacleType.COIN -> 0.01f
            ObstacleType.STAR -> 0.015f
            ObstacleType.DANGER -> 0.02f
        }
        // Apply speed multiplier
        val speed = baseSpeed * obstacleSpeedMultiplier
        Log.d("SPEED", "Current multiplier: $obstacleSpeedMultiplier") //testing
        obstacles.add(Obstacle(x = randomX, y = 0f, type = type, speed = speed))
    }

    private fun checkCollisions() {
        val planeCenterX = plane.x
        val planeCenterY = plane.y
        val planeRadius = plane.width / 2

        val iterator = obstacles.iterator()
        while (iterator.hasNext()) {
            val obstacle = iterator.next()
            val obstacleCenterX = obstacle.x
            val obstacleCenterY = obstacle.y
            
            // Radius depends on type
            val obstacleRadius = when (obstacle.type) {
                ObstacleType.COIN -> 0.12f   
                ObstacleType.DANGER -> 0.108f  
                ObstacleType.STAR -> 0.075f    
            }

            // Simple circle collision detection
            val dx = planeCenterX - obstacleCenterX
            val dy = planeCenterY - obstacleCenterY
            val distance = Math.sqrt((dx * dx + dy * dy).toDouble())

            if (distance < (planeRadius + obstacleRadius)) {
                // Collision detected
                when (obstacle.type) {
                    ObstacleType.COIN -> {
                        score.coins++
                        notifyScoreUpdated()
                    }
                    ObstacleType.STAR -> {
                        score.stars++
                        notifyScoreUpdated()
                    }
                    ObstacleType.DANGER -> {
                        // Game over on danger hit -> NOW subtract life
                        plane.lives--
                        notifyLivesUpdated()
                        
                        if (plane.lives <= 0) {
                            gameState = GameState.GAME_OVER
                            score.total = score.calculateTotal()
                            notifyGameOver(score.total)
                            sensorManager.unregisterListener(this)
                            return
                        }
                    }
                }
                iterator.remove()
                notifyObstaclesUpdated()
            }
        }
    }

    // Accelerometer listener methods
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER && gameState == GameState.PLAYING) {
            // Tilt left/right controls X position
            val tiltX = event.values[0]  // Left/right tilt

            // Update plane position based on tilt
            plane.x += (-tiltX * tiltSensitivity)

            // Keep plane within screen bounds (0 to 1)
            plane.x = plane.x.coerceIn(0.1f, 0.9f)
            
            // Debug Log
            android.util.Log.d("GameSensor", "Tilt: $tiltX, PlaneX: ${plane.x}")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed for this game
    }

    // Game control methods
    fun pauseGame() {
        if (gameState == GameState.PLAYING) {
            gameState = GameState.PAUSED
        }
    }

    fun resumeGame() {
        if (gameState == GameState.PAUSED) {
            gameState = GameState.PLAYING
        }
    }

    fun restartGame() {
        // Reset all game state
        gameState = GameState.PLAYING
        gameStartTime = System.currentTimeMillis()
        plane.x = 0.5f
        plane.lives = 3
        score.coins = 0
        score.stars = 0
        score.timeAlive = 0
        score.total = 0
        obstacles.clear()
        lastObstacleTime = 0

        // Re-register sensor listener if needed
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    // Listener management
    fun addListener(listener: GameStateListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: GameStateListener) {
        listeners.remove(listener)
    }

    private fun notifyScoreUpdated() {
        listeners.forEach { it.onScoreUpdated() }
    }

    private fun notifyLivesUpdated() {
        listeners.forEach { it.onLivesUpdated() }
    }

    private fun notifyObstaclesUpdated() {
        listeners.forEach { it.onObstaclesUpdated() }
    }

    private fun notifyGameOver(finalScore: Int) {
        listeners.forEach { it.onGameOver(finalScore) }
    }

    // Clean up resources
    fun cleanup() {
        sensorManager.unregisterListener(this)
        listeners.clear()
    }

    // Getter for obstacles (read-only)
    fun getObstacles(): List<Obstacle> = obstacles.toList()

}