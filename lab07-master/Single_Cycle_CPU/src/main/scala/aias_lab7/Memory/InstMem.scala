package aias_lab7.Memory

import chisel3._
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile
import firrtl.annotations.MemoryLoadFileType

class InstMem(AddressWidth:Int, AccessBytes:Int) extends Module {
  val io = IO(new Bundle {
    val wen   = Input(UInt(AccessBytes.W))
    val raddr = Input(UInt(AddressWidth.W))
    val waddr = Input(UInt(AddressWidth.W))
    val wdata = Input(UInt((8*AccessBytes).W))
	
	val rdata = Output(UInt((8*AccessBytes).W))
  }) 

//declaration
  val memory = Mem((1<<(AddressWidth)), UInt(8.W))
  loadMemoryFromFile(memory, "./src/main/resource/m_code.hex")
  val AccessIndex = WireInit(VecInit(Seq.range(0,AccessBytes).map{x=>x.U(AddressWidth.W)}))
 
val read_data = Wire(Vec(AccessBytes, UInt(8.W)))

  for(i <- 0 until AccessBytes){
	read_data(i) := 0.U
	when(io.wen(i) === 1.U){memory(io.waddr+AccessIndex(i)) := io.wdata(8*i+7,8*i)} //write operation
	.otherwise{read_data(i) := memory(io.raddr+AccessIndex(i))}//read operation  
  }

io.rdata := read_data.asUInt
}  