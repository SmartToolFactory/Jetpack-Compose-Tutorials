package com.smarttoolfactory.tutorial2_1unit_testing.car

class Car {


    fun drive(direction: Direction): Outcome {

        return when (direction) {
            Direction.NORTH -> Outcome.OK
            Direction.EAST -> Outcome.RECORDED
            Direction.SOUTH -> Outcome.RECORDED
            Direction.WEST -> Outcome.RECORDED
        }
    }

    fun recordTelemetry(
        speed: Int = 0,
        direction: Direction = Direction.NORTH,
        lat: Double = 0.0,
        long: Double = 0.0
    ) = Outcome.RECORDED

    fun door(doorType: DoorType): Car = apply {}

    fun windowState(): WindowState {
        return WindowState.UP
    }

    fun accelerate(fromSpeed: Int, toSpeed: Int) {

    }


}


enum class Direction(azimuth: Int) {

    NORTH(360),
    EAST(90),
    SOUTH(180),
    WEST(270)

}

enum class Outcome {
    OK,
    RECORDED
}

enum class DoorType {
    FRONT_LEFT,
    FRONT_RIGHT,
    BACK_LEFT,
    BACK_RIGHT

}

enum class WindowState {
    UP,
    DOWN
}