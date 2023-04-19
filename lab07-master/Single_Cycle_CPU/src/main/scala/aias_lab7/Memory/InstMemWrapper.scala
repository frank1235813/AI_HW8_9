package aias_lab7.Memory

import chisel3._
import chisel3.util._

class InstMemWrapper(bits:Int) extends Module {
  val io = IO(new Bundle {
    //32kB -> bits = 15
    val raddr = Input(UInt(bits.W))
    val inst = Output(UInt(32.W))
  })
  
  //instantiation
  val im = Module(new InstMem (AddressWidth=bits, AccessBytes=4))
  
  //read operation
  im.io.raddr := Cat(io.raddr(bits-1, 2), 0.U(2.W)) 
  io.inst 	  := im.io.rdata
  
  //write operation All false for IM
  im.io.waddr := 0.U(bits.W)
  im.io.wen   := 0.U(4.W)
  im.io.wdata := 0.U(32.W)
}