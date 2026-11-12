package com.mamxanh.shared

import com.mamxanh.shared.data.sql.*
import kotlin.test.*

class BackupCodecTest {
    @Test fun sha256KnownVector() {
        assertEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",BackupDigest.sha256("abc".encodeToByteArray()))
    }
    @Test fun nullableUnicodeSnapshotRoundTrip() {
        val input=GameBackup(7,"test",1234,listOf(BackupTable("fixture",listOf("name","optional"),
            listOf(listOf("Tiếng Việt 🌱\n:|-1:",null),listOf("","")))))
        assertEquals(input,GameBackupCodec.decode(GameBackupCodec.encode(input)))
    }
    @Test fun trailingContentAndChecksumChangesAreRejected() {
        val bytes=GameBackupCodec.encode(GameBackup(7,"test",1234,emptyList()))
        assertFailsWith<IllegalArgumentException>{GameBackupCodec.decode(bytes+"x".encodeToByteArray())}
    }
    @Test fun futureFormatIsNotSilentlyAccepted() {
        val bytes=GameBackupCodec.encode(GameBackup(7,"test",1234,emptyList()))
        bytes[8]='2'.code.toByte()
        assertFailsWith<IllegalArgumentException>{GameBackupCodec.decode(bytes)}
    }
}
