package aias_lab5.Hw4

import chisel3._
import chisel3.util._

class PC extends Module {
    val io = IO(new Bundle{
        val brtaken = Input(Bool()) 
        val jmptaken =  Input(Bool())
        val offset = Input(UInt(32.W))
        val pc = Output(UInt(32.W))
    })

    val pcReg = RegInit(0.U(32.W))
    pcReg := Mux( (io.brtaken | io.jmptaken), io.offset(31,2)<<2, pcReg + 4.U)
    io.pc := pcReg
}

