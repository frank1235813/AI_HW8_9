package aias_lab7.Memory

import chisel3._
import chisel3.util._

import aias_lab7.Single_Cycle.Mem_op_map._

class DataMemWrapper(bits:Int) extends Module {
  val io = IO(new Bundle {
    val funct3 = Input(UInt(3.W))
    val raddr = Input(UInt(bits.W))
    val rdata = Output(UInt(32.W))
    
    val wen   = Input(Bool())
    val waddr = Input(UInt(bits.W))
    val wdata = Input(UInt(32.W))
  })
  
  //instantiation
  val dm = Module(new DataMem (AddressWidth=bits, AccessBytes=4))
  
  //read operation
  dm.io.raddr := Cat(io.raddr(bits-1, 2), 0.U(2.W)) 
  io.rdata := MuxLookup(io.funct3, dm.io.rdata, Seq(
    WORD 	-> dm.io.rdata,
	HALF 	-> Mux((io.raddr(1) === 1.U), Cat(Fill(16, dm.io.rdata(31)), dm.io.rdata(31, 16)), 
									      Cat(Fill(16, dm.io.rdata(15)), dm.io.rdata(15,  0))
			   ),
	UHALF	-> Mux((io.raddr(1) === 1.U), Cat(0.U(16.W), dm.io.rdata(31, 16)), 
									      Cat(0.U(16.W), dm.io.rdata(15,  0))
			   ),
	BYTE	-> MuxLookup(io.raddr(1,0), dm.io.rdata, Seq(
					0.U -> Cat(Fill(24, dm.io.rdata( 7)), dm.io.rdata( 7, 0)),
					1.U -> Cat(Fill(24, dm.io.rdata(15)), dm.io.rdata(15, 8)),
					2.U -> Cat(Fill(24, dm.io.rdata(23)), dm.io.rdata(23,16)),
					3.U -> Cat(Fill(24, dm.io.rdata(31)), dm.io.rdata(31,24))
			   )),
	UBYTE	-> MuxLookup(io.raddr(1,0), dm.io.rdata, Seq(
					0.U -> Cat(0.U(24.W), dm.io.rdata( 7, 0)),
					1.U -> Cat(0.U(24.W), dm.io.rdata(15, 8)),
					2.U -> Cat(0.U(24.W), dm.io.rdata(23,16)),
					3.U -> Cat(0.U(24.W), dm.io.rdata(31,24))
			   ))			   
  ))
  
  //write operation
  dm.io.waddr := Cat(io.waddr(bits-1, 2), 0.U(2.W))
  when(io.wen){
	  dm.io.wen := MuxLookup(io.funct3, 0.U(4.W), Seq(
		WORD 	-> "b1111".U(4.W),
		HALF 	-> Mux((io.waddr(1) === 1.U), "b1100".U(4.W), 
											  "b0011".U(4.W)
				   ),
		BYTE	-> MuxLookup(io.waddr(1,0), 0.U(4.W), Seq(
						0.U -> "b0001".U(4.W),
						1.U -> "b0010".U(4.W),
						2.U -> "b0100".U(4.W),
						3.U -> "b1000".U(4.W)
				   ))		   
	  ))
  }.otherwise{
	dm.io.wen := 0.U(4.W)
  }
  dm.io.wdata := MuxLookup(io.funct3, io.wdata, Seq(
    WORD 	-> io.wdata,
	HALF 	-> Cat(io.wdata(15, 0), io.wdata(15, 0)),
	BYTE	-> Cat(io.wdata(7, 0), io.wdata(7, 0), io.wdata(7, 0), io.wdata(7, 0))	   
  ))
  
}