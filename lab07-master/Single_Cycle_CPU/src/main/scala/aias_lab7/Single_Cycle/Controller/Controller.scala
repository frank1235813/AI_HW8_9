package aias_lab7.Single_Cycle.Controller

import chisel3._
import chisel3.util._

import aias_lab7.Single_Cycle.opcode_map._
import aias_lab7.Single_Cycle.op_type_map._
import aias_lab7.Single_Cycle.condition_map._
import aias_lab7.Single_Cycle.ALU_op_map._
import aias_lab7.Single_Cycle.WB_select_map._

class Controller extends Module {
    val io = IO(new Bundle{
        val Inst = Input(UInt(32.W))
        val BrEq = Input(Bool())
        val BrLT = Input(Bool())

        val PCSel 	= Output(Bool())
        val ImmSel 	= Output(UInt(3.W))
        val RegWEn 	= Output(Bool())
        val BrUn 	= Output(Bool())
		val ASel 	= Output(UInt(2.W))
        val BSel 	= Output(UInt(1.W))
        val ALUSel 	= Output(UInt(16.W))
        val Mem_W 	= Output(Bool())
		val Mem_R 	= Output(Bool())
        val WBSel 	= Output(UInt(2.W))

        //new
        val Lui = Output(Bool())
        val Hcf = Output(Bool())
    })
    
    val opcode = Wire(UInt(7.W))
    opcode := io.Inst(6,0)

    val funct3 = Wire(UInt(3.W))
    funct3 := io.Inst(14,12)

    val funct7 = Wire(UInt(7.W))
    funct7 := io.Inst(31,25)
	
	val msb_bit12 = Wire(UInt(12.W))
	msb_bit12 := io.Inst(31,20)
	
	val R_type_dafault_op = Wire(UInt(16.W))
	R_type_dafault_op := Cat(0.U(1.W), 0.U(7.W), 0.U(5.W), funct3)
	
	val R_type_default_shifting_op = Wire(UInt(16.W))
	R_type_default_shifting_op := Cat(0.U(1.W), funct7, 0.U(5.W), funct3)
	//determine whether jump
	val Jump_taken = Wire(Bool())
	Jump_taken := MuxLookup(opcode, false.B, Seq(
          BRANCH -> MuxLookup(funct3, false.B, Seq(
				BEQ  ->  io.BrEq,
				BNE  -> !io.BrEq,
				BLT  ->  io.BrLT,
				BGE  -> !io.BrLT,
				BLTU ->  io.BrLT,
				BGEU -> !io.BrLT
		  )),
		  JAL  -> true.B,
		  JALR -> true.B
    ))
    //Control signal
    io.RegWEn := MuxLookup(opcode, false.B, Seq(
		LOAD 	-> true.B,
		JALR 	-> true.B,
		JAL 	-> true.B,
		OP_IMM 	-> true.B,
		OP 		-> true.B,
		AUIPC 	-> true.B,
		LUI 	-> true.B
	))
    io.ASel := MuxLookup(opcode, 0.U, Seq(
		BRANCH 	-> 1.U,
		JAL 	-> 1.U,
		AUIPC 	-> 1.U,
		LUI 	-> 2.U
	)) 
	io.BSel := MuxLookup(opcode, 0.U, Seq(
		LOAD 	-> 1.U,
		STORE 	-> 1.U,
		BRANCH	-> 1.U,
		JALR 	-> 1.U,
		JAL 	-> 1.U,
		OP_IMM 	-> 1.U,
		AUIPC 	-> 1.U,
		LUI 	-> 1.U
	))
	
    io.ImmSel := MuxLookup(opcode, 0.U, Seq(
		LOAD 	-> I_TYPE,
		STORE 	-> S_TYPE,
		BRANCH 	-> B_TYPE, 
		JALR 	-> I_TYPE,
		JAL 	-> J_TYPE,
		OP_IMM 	-> I_TYPE,   
		AUIPC 	-> U_TYPE,
		LUI 	-> U_TYPE
	))
    io.ALUSel := MuxLookup(opcode, (0.U(16.W)), Seq( //default: "ADD" op
					OP 		-> Cat(0.U(1.W), funct7, 0.U(5.W), funct3), //R_TYPE = 0.U, I_TYPE = 1.U
					OP_IMM 	-> MuxCase(R_type_dafault_op, Seq( //default: turn I_TYPE op into R_type operation & no sub op in IMM
								(funct3 === "b101".U) -> MuxCase(R_type_default_shifting_op, Seq( //op:SRLI, SRAI
															(msb_bit12 === "h287".U) -> Cat(1.U(1.W), msb_bit12, funct3),//op: orc.b 
															(msb_bit12 === "h698".U) -> Cat(1.U(1.W), msb_bit12, funct3)//op: rev8
															
														 )),
								(funct3 === "b001".U) -> MuxCase(R_type_default_shifting_op, Seq( //op:SLLI
															(msb_bit12 === "h300".U) -> Cat(1.U(1.W), msb_bit12, funct3),//op: clz
															(msb_bit12 === "h302".U) -> Cat(1.U(1.W), msb_bit12, funct3),//op: cpop
															(msb_bit12 === "h301".U) -> Cat(1.U(1.W), msb_bit12, funct3),//op: ctz
															(msb_bit12 === "h304".U) -> Cat(1.U(1.W), msb_bit12, funct3),//op: sext.b
															(msb_bit12 === "h305".U) -> Cat(1.U(1.W), msb_bit12, funct3)//op: sext.h
														 ))
							  ))
	))
    
    io.WBSel := MuxLookup(opcode, ALUOUT_SIG, Seq(
		LOAD 	-> LD_DATA_SIG,
		JALR 	-> PC_PLUS_4_SIG,
		JAL 	-> PC_PLUS_4_SIG
	))
	
	io.PCSel 	:= Jump_taken
	io.BrUn 	:= io.Inst(13) === 1.U
    io.Mem_W 	:= opcode === STORE
	io.Mem_R	:= opcode === LOAD
    io.Lui		:= opcode === LUI
    io.Hcf 		:= opcode === HCF
}