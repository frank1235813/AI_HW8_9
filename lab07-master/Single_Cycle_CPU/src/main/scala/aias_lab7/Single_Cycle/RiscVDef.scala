package aias_lab7.Single_Cycle

import chisel3._
import chisel3.util._

object opcode_map {
    val LOAD      = "b0000011".U
    val STORE     = "b0100011".U
    val BRANCH    = "b1100011".U
    val JALR      = "b1100111".U
    val JAL       = "b1101111".U
    val OP_IMM    = "b0010011".U
    val OP        = "b0110011".U
    val AUIPC     = "b0010111".U
    val LUI       = "b0110111".U
    val HCF       = "b0001011".U
}

object op_type_map{
	val R_TYPE = 0.U
	val I_TYPE = 1.U
    val S_TYPE = 2.U
    val B_TYPE = 3.U
    val J_TYPE = 4.U
    val U_TYPE = 5.U	
}

object ALU_op_map{ //map {(IMM_TYPE or R_TYPE)(1 bit), funct7, inst(24,20), funct3}
  val ADD  = 0.U
  val SLL  = 1.U
  val SLT  = 2.U
  val SLTU = 3.U
  val XOR  = 4.U
  val SRL  = 5.U
  val OR   = 6.U
  val AND  = 7.U
  val SUB  	 = "b0_0100000_00000_000".U
  val SRA  	 = "b0_0100000_00000_101".U
  val ORCB 	 = "b1_0010_1000_0111_101".U
  val ORN  	 = "b0_0100000_00000_110".U
  val REV8 	 = "b1_0110_1001_1000_101".U
  val ROL  	 = "b0_0110000_00000_001".U
  val ROR  	 = "b0_0110000_00000_101".U
  val MINU 	 = "b0_0000101_00000_101".U
  val XNOR 	 = "b0_0100000_00000_100".U
  val BINV 	 = "b0_0110100_00000_001".U
  val BSET 	 = "b0_0010100_00000_001".U
  val ANDN 	 = "b0_0100000_00000_111".U
  val CLZ  	 = "b1_0110000_00000_001".U
  val CPOP 	 = "b1_0110000_00010_001".U
  val CTZ  	 = "b1_0110000_00001_001".U
  val MAX  	 = "b0_0000101_00000_110".U
  val MAXU 	 = "b0_0000101_00000_111".U
  val MIN  	 = "b0_0000101_00000_100".U
  val SH1ADD = "b0_0010000_00000_010".U
  val SH2ADD = "b0_0010000_00000_100".U
  val SH3ADD = "b0_0010000_00000_110".U
  val BCLR   = "b0_0100100_00000_001".U
  val BEXT   = "b0_0100100_00000_101".U
  val SEXTB	 = "b1_0110000_00100_001".U
  val SEXTH  = "b1_0110000_00101_001".U
  val ZEXTH  = "b0_0000100_00000_100".U
}

object condition_map{
  val BEQ 	= "b000".U
  val BNE 	= "b001".U
  val BLT 	= "b100".U
  val BGE 	= "b101".U
  val BLTU 	= "b110".U
  val BGEU 	= "b111".U
}

object WB_select_map{
  val ALUOUT_SIG	= 0.U
  val LD_DATA_SIG	= 1.U
  val PC_PLUS_4_SIG	= 2.U
}

object Mem_op_map{
  val BYTE 	= 0.U
  val HALF 	= 1.U
  val WORD 	= 2.U
  val UBYTE = 4.U
  val UHALF = 5.U
}