package com.example.finalproject
//GameView displays stuff...
//Create a custom View that draws the game
//Connect it to the GameModel
//Handle the drawing of plane, obstacles, and score
//Set up the game loop
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class GameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), GameModel.GameStateListener {

    // Bitmaps for game objects - will load from res/drawable
    private var planeBitmap: Bitmap? = null
    private var coinBitmap: Bitmap? = null
    private var starBitmap: Bitmap? = null
    private var dangerBitmap: Bitmap? = null

    // Paint for text and drawing
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 50f
        typeface = Typeface.DEFAULT_BOLD
    }

    private val gameOverPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 80f
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
    }

    private val backgroundPaint = Paint()

    // Game model reference
    private var gameModel: GameModel? = null

    // Game over state
    private var gameOver = false
    private var finalScore = 0

    // Obstacle sizes (in pixels)
    private val obstacleSize = 80f

    init {
        // Load obstacle images from resources
        loadObstacleImages()

        // Create a sky-like gradient background
        createBackgroundPaint()
    }

    private fun loadObstacleImages() {
        try {
            // Load coin image
            coinBitmap = BitmapFactory.decodeResource(resources, R.drawable.coin)

            // Load star image
            starBitmap = BitmapFactory.decodeResource(resources, R.drawable.star)

            // Load danger/obstacle image
            dangerBitmap = BitmapFactory.decodeResource(resources, R.drawable.bird)

        } catch (e: Exception) {
            // If images don't exist, create colored circles as fallback
            createFallbackBitmaps()
        }
    }

    private fun createFallbackBitmaps() {
        // Create fallback bitmaps if image files are missing
        coinBitmap = createColoredCircleBitmap(Color.YELLOW, obstacleSize.toInt())
        starBitmap = createColoredCircleBitmap(Color.CYAN, obstacleSize.toInt())
        dangerBitmap = createColoredCircleBitmap(Color.RED, obstacleSize.toInt())
    }

    private fun createColoredCircleBitmap(color: Int, size: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            style = Paint.Style.FILL
        }
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)
        return bitmap
    }

    private fun createBackgroundPaint() {
        backgroundPaint.shader = LinearGradient(
            0f, 0f, 0f, 1000f,  // Adjust height as needed
            Color.parseColor("#87CEEB"),  // Light sky blue (top)
            Color.parseColor("#E0F7FF"),  // Very light blue (bottom)
            Shader.TileMode.CLAMP
        )
    }

    // Connect this view to the game model
    fun setGameModel(model: GameModel) {
        this.gameModel = model
        model.addListener(this)
    }

    // Set plane type - loads appropriate plane image
    fun setPlaneType(type: String) {
        planeBitmap = loadPlaneBitmap(type)
        invalidate()
    }

    private fun loadPlaneBitmap(type: String): Bitmap {
        // Try to load from resources first
        val resId = when (type) {
            "plane1" -> R.drawable.plane_black
            "plane2" -> R.drawable.plane_blue
            "plane3" -> R.drawable.plane_red
            else -> R.drawable.plane_black
        }

        return try {
            // Try to load the image
            BitmapFactory.decodeResource(resources, resId)
        } catch (e: Exception) {
            // If image doesn't exist, create a colored placeholder
            createPlanePlaceholder(type)
        }
    }

    private fun createPlanePlaceholder(type: String): Bitmap {
        val size = 120
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = when (type) {
                "plane1" -> Color.RED
                "plane2" -> Color.GREEN
                "plane3" -> Color.MAGENTA
                else -> Color.RED
            }
        }

        // Draw airplane body (ellipse)
        canvas.drawOval(20f, 30f, 100f, 90f, paint)

        // Draw wings
        canvas.drawRect(0f, 50f, 120f, 70f, paint)

        // Draw tail
        canvas.drawRect(90f, 30f, 110f, 50f, paint)

        return bitmap
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val model = gameModel ?: return

        // Draw background
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)

        // Draw some clouds (optional decoration)
        drawClouds(canvas)

        // Draw plane
        drawPlane(canvas, model.plane)

        // Draw obstacles
        for (obstacle in model.getObstacles()) {
            drawObstacle(canvas, obstacle)
        }

        // Draw score
        drawScore(canvas, model.score)

        // Draw game over if needed
        if (gameOver) {
            drawGameOver(canvas)
        }
    }

    private fun drawClouds(canvas: Canvas) {
        // Draw some simple cloud shapes (optional background decoration)
        val cloudPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            alpha = 100  // Semi-transparent
        }

        // Cloud 1
        canvas.drawCircle(100f, 150f, 40f, cloudPaint)
        canvas.drawCircle(140f, 130f, 50f, cloudPaint)
        canvas.drawCircle(180f, 150f, 40f, cloudPaint)

        // Cloud 2
        canvas.drawCircle(width - 200f, 300f, 40f, cloudPaint)
        canvas.drawCircle(width - 160f, 280f, 50f, cloudPaint)
        canvas.drawCircle(width - 120f, 300f, 40f, cloudPaint)
    }

    private fun drawPlane(canvas: Canvas, plane: Plane) {
        planeBitmap?.let { bitmap ->
            // Calculate position (center the plane)
            val x = plane.x * width - bitmap.width / 2
            val y = plane.y * height - bitmap.height / 2

            // Draw the plane
            canvas.drawBitmap(bitmap, x, y, null)

            // Optional: Draw a shadow
            val shadowPaint = Paint().apply {
                color = Color.argb(100, 0, 0, 0)
            }
            canvas.drawCircle(
                plane.x * width,
                plane.y * height + bitmap.height / 3,
                bitmap.width / 4f,
                shadowPaint
            )
        }
    }

    private fun drawObstacle(canvas: Canvas, obstacle: Obstacle) {
        val x = obstacle.x * width
        val y = obstacle.y * height

        val bitmap = when (obstacle.type) {
            ObstacleType.COIN -> coinBitmap
            ObstacleType.STAR -> starBitmap
            ObstacleType.DANGER -> dangerBitmap
        }

        bitmap?.let {
            // Calculate position (center the obstacle)
            val drawX = x - it.width / 2
            val drawY = y - it.height / 2

            // Draw the obstacle
            canvas.drawBitmap(it, drawX, drawY, null)

            // Add a pulsing effect to coins and stars
            if (obstacle.type != ObstacleType.DANGER) {
                val pulsePaint = Paint().apply {
                    color = Color.argb(50, 255, 255, 255)
                    style = Paint.Style.STROKE
                    strokeWidth = 3f
                }
                val time = System.currentTimeMillis() % 1000
                val pulseScale = 1 + 0.1f * kotlin.math.sin(time * 0.01f).toFloat()
                val pulseSize = it.width * pulseScale / 2
                canvas.drawCircle(x, y, pulseSize, pulsePaint)
            }
        }
    }

    private fun drawScore(canvas: Canvas, score: Score) {
        // Background for score display
        val scoreBgPaint = Paint().apply {
            color = Color.argb(150, 0, 0, 0)
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(10f, 10f, 350f, 200f, 20f, 20f, scoreBgPaint)

        // Draw score labels with icons
        textPaint.color = Color.WHITE

        // Coins
        canvas.drawText("💰 Coins: ${score.coins}", 30f, 60f, textPaint)

        // Stars
        canvas.drawText("⭐ Stars: ${score.stars}", 30f, 120f, textPaint)

        // Time
        val seconds = score.timeAlive / 1000
        canvas.drawText("⏱️ Time: ${seconds}s", 30f, 180f, textPaint)

        // Total score (right side)
        val totalScoreBg = Paint().apply {
            color = Color.argb(200, 255, 193, 7)  // Amber color
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(
            width - 250f, 10f,
            width.toFloat() - 10f, 100f,
            20f, 20f,
            totalScoreBg
        )

        textPaint.color = Color.BLACK
        textPaint.textAlign = Paint.Align.CENTER
        canvas.drawText(
            "SCORE",
            width - 130f,
            50f,
            textPaint
        )

        textPaint.textSize = 40f
        canvas.drawText(
            "${score.total}",
            width - 130f,
            90f,
            textPaint
        )

        // Reset text paint
        textPaint.textAlign = Paint.Align.LEFT
        textPaint.color = Color.WHITE
        textPaint.textSize = 50f
    }

    private fun drawGameOver(canvas: Canvas) {
        // Semi-transparent overlay
        val overlayPaint = Paint().apply {
            color = Color.argb(200, 0, 0, 0)
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), overlayPaint)

        // Game over text
        val centerX = width / 2f

        gameOverPaint.color = Color.WHITE
        canvas.drawText("✈️ GAME OVER ✈️", centerX, height / 3f, gameOverPaint)

        // Final score
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.color = Color.YELLOW
        textPaint.textSize = 60f
        canvas.drawText("Final Score: $finalScore", centerX, height / 3f + 100f, textPaint)

        // Restart hint
        textPaint.color = Color.CYAN
        textPaint.textSize = 40f
        canvas.drawText("Tap screen to restart", centerX, height / 3f + 180f, textPaint)

        // Reset text paint
        textPaint.textAlign = Paint.Align.LEFT
        textPaint.color = Color.WHITE
        textPaint.textSize = 50f
    }

    // Implement GameStateListener methods
    override fun onScoreUpdated() {
        invalidate() // Redraw to show updated score
    }

    override fun onObstaclesUpdated() {
        invalidate() // Redraw to show moving obstacles
    }

    override fun onGameOver(finalScore: Int) {
        this.gameOver = true
        this.finalScore = finalScore
        invalidate()
    }

    // Call this when user taps to restart
    fun restartGame() {
        gameOver = false
        gameModel?.restartGame()
        invalidate()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        gameModel?.removeListener(this)
    }
}