package aias_lab5.Hw4

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
}

import opcode_map._

class Decoder extends Module{
    val io = IO(new Bundle{
        val inst = Input(UInt(32.W))

        //Please fill in the blanks by yourself
        val funct7 = Output(UInt(7.W))
        val funct3 = Output(UInt(3.W))
        val rs1 = Output(UInt(5.W))
        //Please fill in the blanks by yourself
        val rd = Output(UInt(5.W))
        val opcode = Output(UInt(7.W))
        val imm = Output(SInt(32.W))
        
        val ctrl_RegWEn = Output(Bool())    // for Reg write back
        val ctrl_ASel = Output(Bool())      // for alu src1
        val ctrl_BSel = Output(Bool())      // for alu src2
        val ctrl_Br = Output(Bool())        // for branch inst.
        val ctrl_Jmp = Output(Bool())       // for jump inst.
        val ctrl_Lui = Output(Bool())       // for lui inst.
        val ctrl_MemRW = Output(Bool())     // for L/S inst
        val ctrl_WBSel = Output(Bool())
    })

    //Please fill in the blanks by yourself
    io.funct7 := io.inst(31,25)
    io.funct3 := io.inst(14,12)
    
    io.rs1 := io.inst(19,15)
    //Please fill in the blanks by yourself
    io.rd := io.inst(11,7)
    io.opcode := io.inst(6,0)

    //ImmGen
    io.imm := MuxLookup(io.opcode,0.S,Seq(
        //R-type
        //Please fill in the blanks by yourself
        OP -> io.inst(31,20).asSInt,
        //I-type
        OP_IMM -> io.inst(31,20).asSInt,
        //Please fill in the blanks by yourself
        JALR -> io.inst(31,20).asSInt,
        //Please fill in the blanks by yourself
        LOAD -> io.inst(31,20).asSInt,
        
        //B-type
        //Please fill in the blanks by yourself
        BRANCH -> ( ( io.inst(31).asUInt << 12 ) | ( io.inst(7).asUInt << 11 ) |
                    ( io.inst(30,25).asUInt << 5 ) | ( io.inst(11,8).asUInt << 1 ) ).asSInt ,
        
        //S-type
        //Please fill in the blanks by yourself
        STORE -> ( ( io.inst(31,25).asUInt << 5 ) | io.inst(11,7).asUInt ).asSInt,

        //U-type
        //Please fill in the blanks by yourself
        LUI -> (io.inst(31,12).asSInt << 12),
        //Please fill in the blanks by yourself
        AUIPC -> (io.inst(31,12).asSInt << 12), 

        //J-type
        //Please fill in the blanks by yourself
        JAL -> ( ( ( io.inst(31).asUInt << 19 ) | ( io.inst(19,12).asUInt << 11 ) | 
                  ( io.inst(20).asUInt << 10 ) | ( io.inst(30,21).asUInt ) ) << 1).asSInt
        
    ))

    //Controller
    io.ctrl_RegWEn := false.B
    io.ctrl_ASel := true.B                                                              // to choose ALU_src1
    io.ctrl_BSel := Mux(io.opcode===OP_IMM,false.B, true.B)                             // to choose ALU_src2
    io.ctrl_Br := Mux(io.opcode===BRANCH,true.B, false.B)
    io.ctrl_Jmp := Mux((io.opcode===JAL) || (io.opcode===JALR),true.B, false.B)
    io.ctrl_Lui := Mux(io.opcode===LUI,true.B, false.B)
    io.ctrl_MemRW := Mux((io.opcode===STORE),true.B, false.B)                           // 0:Read 1:Write
    io.ctrl_WBSel := Mux((io.opcode===STORE) || (io.opcode===LOAD), false.B, true.B)    // true: from alu , false: from dm , another source? 
}