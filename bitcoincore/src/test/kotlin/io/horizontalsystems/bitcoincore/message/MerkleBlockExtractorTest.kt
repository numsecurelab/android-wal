package io.horizontalsystems.bitcoincore.message

import io.horizontalsystems.bitcoincore.blocks.MerkleBlockExtractor
import io.horizontalsystems.bitcoincore.core.DoubleSha256Hasher
import io.horizontalsystems.bitcoincore.extensions.toHexString
import io.horizontalsystems.bitcoincore.network.messages.MerkleBlockMessage
import io.horizontalsystems.bitcoincore.network.messages.MerkleBlockMessageParser
import io.horizontalsystems.bitcoincore.serializers.BlockHeaderParser
import org.junit.Assert
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class MerkleBlockExtractorTest : Spek({
    val merkleRootHasher = DoubleSha256Hasher()
    val blockHeaderParser = BlockHeaderParser(merkleRootHasher)
    val messageParser = MerkleBlockMessageParser(blockHeaderParser)

    val merkleBlockExtractor by memoized {
        MerkleBlockExtractor(1_000_000)
    }

    describe("#extract") {

        it("extract associated transactions") {
            val data = byteArrayOf(0, 0, 0, 32, 109, -94, 121, -122, 45, -39, -27, -60, 114, -54, -4, 71, 111, -80, 31, 78, 121, 112, 57, 123, 31, 11, 54, 0, 0, 0, 0, 0, 0, 0, 0, 0, 62, 20, 113, -25, -9, -115, -34, 71, 115, 26, 108, -21, -128, 93, 118, 111, 64, -9, 83, 80, -89, 94, 21, 101, -16, -14, -98, 92, -116, -67, -35, -71, 0, 38, 13, 91, 73, 90, 65, 23, -94, -118, -41, 117, -104, 1, 0, 0, 40, -79, 100, -36, 63, -28, 61, -74, -33, 78, 5, 40, -66, -31, 51, -44, 77, -116, -115, -110, -108, 104, 91, 75, 40, -102, -54, -116, 89, 55, 24, 50, -96, 12, 63, 20, 25, -82, 66, 103, -121, 49, -92, -114, -84, 78, 86, 98, 111, 64, 3, -19, 115, 6, 10, 48, -6, 4, -30, 9, 66, -25, -127, 12, 83, 4, -18, -84, 32, 97, -6, -80, -54, 53, -8, -19, -95, 3, 123, -76, 51, -11, 16, 107, -37, 124, -11, 22, 0, -105, -103, 53, -31, 6, 5, 118, 116, -93, 25, 88, -53, 103, -1, -62, 116, 43, 64, -98, 87, 113, 100, -109, -105, -16, 23, -3, -31, 75, -125, 123, -57, 30, -126, -69, 19, 10, 0, 126, -119, -25, -59, 29, 107, -74, -64, -70, 32, 120, -118, -58, -90, 122, 67, 55, 4, 69, -81, -53, 48, 47, -75, 46, -53, 59, -106, -24, -54, 31, -78, 81, 29, 33, -49, -105, 94, -119, -122, -98, -12, 64, -63, 95, -27, -99, -105, 42, -63, -42, -117, 79, -103, 36, -72, -67, 1, -41, 70, 62, -8, 29, 120, 7, 58, -38, 46, -97, -107, 51, 86, -41, 101, -9, 97, -57, -37, 66, 9, 61, 79, -112, -59, 123, 49, 86, -8, -86, -63, 57, -65, 103, -30, 106, -120, -77, 60, -7, 88, 66, 53, -51, 81, 110, 70, 46, 91, 7, 124, -44, -23, 104, -55, 74, 54, 62, 60, -21, 55, -61, 99, -72, 7, 53, -66, 105, -24, 121, -60, 15, 8, -37, -85, 52, -1, -56, -10, 15, -118, -76, -94, 3, 54, -23, 69, -36, -118, 43, -20, -121, 106, 83, 76, -41, -65, -8, -109, -30, -41, -89, 24, 16, -98, 19, -97, 125, 91, -73, -21, 56, -3, 89, -111, -128, -75, 54, -5, -1, 55, 72, -51, -93, -127, -42, -12, -22, -118, -32, 19, 114, -69, 45, 119, -39, 37, 0, -23, -18, -14, -103, -14, 53, 72, -93, -113, -80, 21, -127, -49, -66, -50, 97, 38, -105, -5, -27, 67, -83, 16, 15, 106, 69, -127, -61, -98, 9, 118, -94, -89, 91, -22, 118, 76, -44, -85, 66, -18, -107, 92, -124, 46, -112, -117, 61, -96, 66, -33, 61, -74, -48, 37, 54, 48, 127, 13, 72, -123, -33, 121, 63, 74, -47, 56, 18, -96, -123, 4, -59, 15, 56, 88, 57, 21, -8, -99, 80, 107, 3, -79, -1, 116, 113, -43, 25, -20, -120, -20, -75, 55, -53, 78, 61, -48, 38, -75, 4, 62, 25, -68, 115, -97, 43, 75, 44, -9, -1, -82, 33, -116, -4, 102, -71, 68, -95, 25, 110, -7, 113, 9, 19, 99, 109, -118, 41, 48, 110, 77, 7, -60, -60, 88, -14, -110, -32, 48, 48, -12, 56, -21, -77, 21, 122, 52, 3, -87, -105, -59, -8, 72, 69, 63, 105, -18, 67, -120, 125, -81, 27, -28, -40, 99, 28, -124, -106, -37, 33, -70, -71, -89, -18, -14, -29, 3, -70, -85, 52, -94, 122, -104, -53, 18, 117, 18, 109, -112, 41, 10, 0, -41, -32, 70, 12, 1, 78, -102, -43, -63, -119, 46, -93, -38, -73, 13, 68, 112, 89, 90, 119, 40, -18, -97, -99, -26, 21, -106, 91, -3, 121, 114, -29, -21, -40, -29, 106, -72, -101, -102, -62, -23, 28, 107, 125, -92, -90, 120, 64, -115, -73, -21, 121, -36, -118, 87, 18, 21, 51, -82, 29, 100, 34, 50, -89, 59, -105, -74, -82, -94, -121, 37, 5, -123, -37, -106, 47, -10, -29, 31, 13, -38, 0, 93, 41, -86, -11, -14, 1, -35, 83, 58, 114, 25, 53, -121, 100, -71, -98, -60, 53, -119, 84, -123, 106, -16, 22, 36, 64, 56, 94, -42, 122, -71, -79, -85, -3, -29, 81, -109, -47, -95, 32, 23, 45, -8, 34, 86, 31, 49, 18, -19, 102, 57, 82, 29, -110, 77, -117, 35, -37, 0, 61, 42, 115, -9, -23, 84, -54, -28, 32, -116, -51, 122, 116, -25, 70, 113, -21, -17, -62, -118, -15, -83, -13, -77, -60, -16, -82, -123, 7, -63, 74, -92, 114, 41, -33, 119, 24, 75, 115, -38, -50, -87, 97, 99, 106, 9, -98, 101, -102, -91, -36, 77, 15, -12, -70, -69, 92, -53, 3, -69, 15, 60, -35, 53, 0, 3, -123, 87, -77, 80, -41, -89, 50, -4, 100, -6, -75, 98, -98, 30, 29, 30, 81, -25, 20, 38, 28, 110, 110, 117, 83, -11, -88, -31, 81, 106, 64, -84, -89, 111, -116, -28, 44, 52, 99, 15, 3, 10, -30, 103, -111, 78, 36, -125, 122, -42, 42, 46, -93, 3, -67, -104, -28, 22, -50, 112, -38, -117, 43, -63, 30, 65, 12, 8, -18, -108, 65, 117, -86, 118, 42, 18, -122, 123, -118, -55, 104, 57, 10, -17, -118, 117, 70, 50, 2, -75, 39, 92, -98, -96, 62, 36, -104, 10, 80, 95, -11, 96, -109, 3, -23, 26, 121, -22, 104, -47, -49, 89, -120, -8, 101, 119, -43, -50, -75, -84, -72, 99, 100, -56, 71, 23, -67, 120, 71, -37, 35, 83, -65, -18, -108, 68, -28, -47, 105, -35, 42, 5, -11, -22, -88, -68, -81, 116, -39, -21, 118, 80, 110, 38, 50, -81, -20, -110, 67, -6, -87, -17, -18, -119, -16, -106, -25, -91, -59, -37, 71, -121, -115, -45, -108, -44, -105, 23, 90, 88, -1, 81, 17, -55, -126, -28, -102, 121, 57, 22, 54, 38, 56, 95, 117, 94, 83, -100, -55, 35, -76, 114, -116, -20, -24, 122, -107, -15, -10, 8, 123, -92, -128, -67, 99, -18, 17, -104, -36, -118, 56, 7, 125, 79, 61, -10, 77, 125, 124, -34, 15, 99, -111, 57, 57, -66, -119, 15, -105, 74, 94, 59, -25, -94, -64, 71, -9, -95, 25, 2, -118, -66, -11, 8, 93, 48, 24, -43, 108, 1, -89, -43, -126, -87, -78, 123, -96, -8, 82, 90, -93, 45, 116, 6, -83, 101, -97, 77, -44, -106, 15, -17, 62, -82, 125, 117, 103, -86, 19, 29, 46, -112, 58, 92, 91, -119, -112, -110, 76, -95, -99, 16, -35, 79, -48, 104, 4, -48, -41, 127, 44, -103, -118, 50, 54, -97, -6, 103, -25, -23, 22, 32, -24, -121, 21, 66, 57, 38, -66, 62, 125, -96, 63, -54, -2, -107, 86, 41, -33, -13, -89, -83, -78, -79, 67, 101, 85, 112, -78, -9, 53, 13, -25, -116, 97, -85, -12, -10, 75, -49, 8, -36, -37, 28, 68, -44, -38, -124, -85, -43, -4, -106, -118, 120, 116, 18, -113, -77, -39, 19, -39, 60, -44, -40, -51, 11, -96, 10, -47, 46, 105, 94, 75, -100, -120, -13, 124, -2, 73, 104, -35, -37, 1, -68, -99, -5, -128, 23, 65, 46, 3, 86, -67, 31, -43, 72, 41, -124, -95, 68, 99, 27, -60, 55, 115, 62, 25, -66, -16, -50, -10, -33, -30, -23, -90, -88, 9, 34, -80, 72, 34, -8, -109, -109, 20, 22, -12, 109, 71, -99, 103, 14, 70, 92, 24, -27, 103, -54, 36, -48, 14, 13, -22, -74, -19, 39, 74, 96, -93, 88, 118, -106, -63, 3, -107, -79, -127, -24, 6, -9, -34, 52, 32, -2, 11, 111, -125, -63, 25, -14, 108, 123, 24, 52, 118, -31, 108, 4, 124, -71, 93, -110, 63, 21, 57, -11, 92, -52, 127, -69, -102, -8, 23, -62, 78, 88, 56, -32, -43, 116, 14, 116, 100, 34, -113, -77, -31, -27, 98, -8, 50, -95, -90, -18, -30, 55, 124, 33, -102, -16, 88, -66, 115, -97, 40, 5, -79, 5, 103, -93, -32, 48, -122, -118, -121, -20, 19, -36, 10, -33, 42, -66, -123, 85, -3, -61, -73, 121, 1)

            val expectedTransactionHashes = arrayOf(
                    "e7c51d6bb6c0ba20788ac6a67a43370445afcb302fb52ecb3b96e8ca1fb2511d",
                    "d92500e9eef299f23548a38fb01581cfbece612697fbe543ad100f6a4581c39e",
                    "22561f3112ed6639521d924d8b23db003d2a73f7e954cae4208ccd7a74e74671",
                    "ebefc28af1adf3b3c4f0ae8507c14aa47229df77184b73dacea961636a099e65",
                    "9aa5dc4d0ff4babb5ccb03bb0f3cdd3500038557b350d7a732fc64fab5629e1e",
                    "95f1f6087ba480bd63ee1198dc8a38077d4f3df64d7d7cde0f63913939be890f",
                    "fe955629dff3a7adb2b143655570b2f7350de78c61abf4f64bcf08dcdb1c44d4"
            )

            val expectedBlockHash = "490e924edc714fe5014d6b7f01a86aced7c37b2117a229000000000000000000"

            val message = messageParser.parseMessage(data)
            val merkleBlock = merkleBlockExtractor.extract(message as MerkleBlockMessage)

            Assert.assertEquals(expectedBlockHash, merkleBlock.blockHash.toHexString())
            Assert.assertArrayEquals(expectedTransactionHashes, merkleBlock.associatedTransactionHexes.toTypedArray())
        }
    }
})
