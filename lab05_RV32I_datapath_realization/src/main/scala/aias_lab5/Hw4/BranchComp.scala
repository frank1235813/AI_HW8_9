package aias_lab5.Hw4

import chisel3._
import chisel3.util._

object condition{
  val EQ = "b000".U
  val NE = "b001".U
  val LT = "b100".U
  val GE = "b101".U
  val LTU = "b110".U
  val GEU = "b111".U
}

import condition._

class BranchComp extends Module{
    val io = IO(new Bundle{
        val en = Input(Bool())
        val funct3 = Input(UInt(3.W))
        val src1 = Input(UInt(32.W))
        val src2 = Input(UInt(32.W))

        val brtaken = Output(Bool()) //for pc.io.brtaken
    })
    
    //please implement your code below
    io.brtaken := Mux(io.en===false.B, false.B, MuxLookup(io.funct3,false.B,Seq(
      EQ  -> Mux(io.src1===io.src2, true.B, false.B),
      NE  -> Mux(io.src1===io.src2, false.B, true.B),
      LT  -> Mux(io.src1.asSInt<io.src2.asSInt, true.B, false.B),
      GE  -> Mux(io.src1.asSInt>io.src2.asSInt, true.B, false.B),
      LTU -> Mux(io.src1<io.src2, true.B, false.B),
      GEU -> Mux(io.src1>io.src2, true.B, false.B)
    )))
}