package aias_lab7.Single_Cycle.Datapath

import chisel3._
import chisel3.util._ 

import aias_lab7.Single_Cycle.opcode_map._
import aias_lab7.Single_Cycle.ALU_op_map._

class ALUIO extends Bundle{
  val src1    = Input(UInt(32.W))
  val src2    = Input(UInt(32.W))
  val ALUSel  = Input(UInt(16.W))
  
  val out  = Output(UInt(32.W))
}

class ALU extends Module{
  val io = IO(new ALUIO)
  //********************************Num1 Another signal Declaration*******************************//
	
	
  //*****************************End of Num1 Another signal Declaration***************************//
  //********************************Num1 Another signal circuit*******************************//
	
	
  //*****************************End of Num1 Another signal circuit***************************//
  //********************************Num2 Another signal Declaration*******************************//
	
	
  //*****************************End of Num2 Another signal Declaration***************************//
  //********************************Num2 Another signal circuit*******************************//
	
	
  //*****************************End of Num2 Another signal circuit***************************//
  io.out := 0.U
  switch(io.ALUSel){
    is(ADD ){io.out := io.src1+io.src2}
    is(SLL ){io.out := io.src1 << io.src2(4,0)}
    is(SLT ){io.out := Mux(io.src1.asSInt<io.src2.asSInt,1.U,0.U)}
    is(SLTU){io.out := Mux(io.src1<io.src2              ,1.U,0.U)}
    is(XOR ){io.out := io.src1^io.src2}
    is(SRL ){io.out := io.src1 >> io.src2(4,0)}
    is(OR  ){io.out := io.src1|io.src2}
    is(AND ){io.out := io.src1&io.src2}
    is(SUB ){io.out := io.src1-io.src2}
    is(SRA ){io.out := (io.src1.asSInt >> io.src2(4,0)).asUInt}
	//********************************Num1 ALU implementation*******************************//
	
	
	//*****************************End of Num1 ALU implementation***************************//
	//********************************Num2 ALU implementation*******************************//
	
	
	//*****************************End of Num2 ALU implementation***************************//
  }
}

