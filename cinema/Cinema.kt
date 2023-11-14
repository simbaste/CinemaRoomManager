package cinema

import java.lang.Exception

class ScreenRoom(rows: Int, private val seatsPerRow: Int) {
    private val room = Array(rows) { CharArray(seatsPerRow) { 'S' } }
    private val seats = rows * seatsPerRow

    private val frontHalfRows = if (seats <= 60) rows else rows / 2
    private val backHalfRows = rows - frontHalfRows

    val purchasedTickets: Int
        get() = room.fold(0) { acc, row ->
            acc + row.count { it == 'B' }
        }

    val percentage: Double
        get() = purchasedTickets.toDouble() / seats * 100

    val currentIncome: Int
        get() {
            val frontHalfSeats = room.take(frontHalfRows).fold(0) { acc, row ->
                acc + row.count { it == 'B' }
            }
            val backHalfSeats = room.takeLast(backHalfRows).fold(0) { acc, row ->
                acc + row.count { it == 'B' }
            }
            return ((frontHalfSeats * 10) + (backHalfSeats * 8))
        }

    val totalIncome: Int by lazy {
        if (seats <= 60) {
            seats * 10
        } else {
            (10 * frontHalfRows * seatsPerRow) + (8 * backHalfRows * seatsPerRow)
        }
    }

    fun print() {
        println("Cinema:")
        println("  ${(1..seatsPerRow).joinToString(" ")}")
        room.forEachIndexed { index, row ->
            println("${index+1} ${row.joinToString(" ")}")
        }
        println()
    }

    fun getPriceOfSeatAt(coordinate: Coordinate): Int {
        if (coordinateInRoom(coordinate.realCoordinate())) {
            if (coordinate.y <= frontHalfRows) {
                return 10
            }
            return 8
        }
        return -1
    }

    fun setSeat(coordinate: Coordinate) {
        val realCoordinate = coordinate.realCoordinate()
        if (coordinateInRoom(realCoordinate)) {
            if (room[realCoordinate.y][realCoordinate.x] == 'B') {
                throw TicketAlreadyPurchased()
            }
            room[realCoordinate.y][realCoordinate.x] = 'B'
        } else {
            throw WrongInput()
        }
    }

    private fun coordinateInRoom(coordinate: Coordinate): Boolean {
        return coordinate.y in room.indices && coordinate.x in room[coordinate.y].indices
    }
}

data class Coordinate(val x: Int, val y: Int) {
    fun realCoordinate() = Coordinate(x-1, y-1)
}

class TicketAlreadyPurchased: Exception("That ticket has already been purchased!")
class WrongInput: Exception("Wrong input!")

class MovieTheater {
    private lateinit var screenRoom: ScreenRoom

    private var isRunning = true

    private fun printRoom() {
        screenRoom.print()
    }

    fun start() {
        println("Enter the number of rows:")
        val rows = readln().toInt()
        println("Enter the number of seats in each row:")
        val seatsPerRow = readln().toInt()
        screenRoom = ScreenRoom(rows, seatsPerRow)
        run()

    }

    private fun buyATicket() {
        var isBought = false
        while (!isBought) {
            try {
                println("Enter a row number:")
                val rowNumber = readln().toInt()
                println("Enter a seat number in that row:")
                val seatNumber = readln().toInt()
                val coordinate = Coordinate(seatNumber, rowNumber)
                screenRoom.setSeat(coordinate)

                println("Ticket price: \$${screenRoom.getPriceOfSeatAt(coordinate)}\n")
                isBought = true
            } catch (e: Exception) {
                println(e.message)
            }
        }

    }

    private fun printStats() {
        println("Number of purchased tickets: ${screenRoom.purchasedTickets}")
        println("Percentage: ${String.format("%.2f", screenRoom.percentage)}%")
        println("Current income: \$${screenRoom.currentIncome}")
        println("Total income: \$${screenRoom.totalIncome}")
        println()
    }

    private fun run () {
        while (isRunning) {
            try {
                println("1. Show the seats")
                println("2. Buy a ticket")
                println("3. Statistics")
                println("0. Exit")
                val choice = readln().toInt()
                when (choice) {
                    1 -> printRoom()
                    2 -> buyATicket()
                    3 -> printStats()
                    else -> {
                        isRunning = false
                    }
                }
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }
}

fun main() {
    val movieTheater = MovieTheater()
    movieTheater.start()
}