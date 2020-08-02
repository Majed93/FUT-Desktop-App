package com.fut.api.fut.service;


import com.fut.desktop.app.services.base.IHasher;

public class HasherImpl implements IHasher {
    private static final int[] R1Shifts = {7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22};
    private static final int[] R2Shifts = {5, 9, 14, 20, 5, 9, 14, 20, 5, 9, 14, 20, 5, 9, 14, 20};
    private static final int[] R3Shifts = {4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23};
    private static final int[] R4Shifts = {6, 10, 15, 21, 6, 10, 15, 21, 6, 10, 15, 21, 6, 10, 15, 21};
    private static final String HexCharacters = "0123456789abcdef";

    @Override
    public String hash(String securityAnswer) {
        if (securityAnswer == null || securityAnswer.isEmpty())
            throw new IllegalArgumentException("Invalid security answer");

        String input = CleanString(securityAnswer);

        return input.isEmpty() ? "" : CalculateHash(input);
    }

    private static String CleanString(String input) {
        String output = input.replaceAll("^\\s*", "");
        output = output.replaceAll("\\s*$", "");

        return output.replaceAll("\\s{2,}", " ");
    }

    private static String CalculateHash(String input) {
        int[] chunks = ChunkInput(input);

        int a = 1732584193;
        int b = -271733879;
        int c = -1732584194;
        int d = 271733878;

        for (int i = 0; i < chunks.length; i += 16) {
            int tempA = a;
            int tempB = b;
            int tempC = c;
            int tempD = d;
            a = Ff(a, b, c, d, chunks[i + 0], R1Shifts[0], -680876936);
            d = Ff(d, a, b, c, chunks[i + 1], R1Shifts[1], -389564586);
            c = Ff(c, d, a, b, chunks[i + 2], R1Shifts[2], 606105819);
            b = Ff(b, c, d, a, chunks[i + 3], R1Shifts[3], -1044525330);
            a = Ff(a, b, c, d, chunks[i + 4], R1Shifts[4], -176418897);
            d = Ff(d, a, b, c, chunks[i + 5], R1Shifts[5], 1200080426);
            c = Ff(c, d, a, b, chunks[i + 6], R1Shifts[6], -1473231341);
            b = Ff(b, c, d, a, chunks[i + 7], R1Shifts[7], -45705983);
            a = Ff(a, b, c, d, chunks[i + 8], R1Shifts[8], 1770035416);
            d = Ff(d, a, b, c, chunks[i + 9], R1Shifts[9], -1958414417);
            c = Ff(c, d, a, b, chunks[i + 10], R1Shifts[10], -42063);
            b = Ff(b, c, d, a, chunks[i + 11], R1Shifts[11], -1990404162);
            a = Ff(a, b, c, d, chunks[i + 12], R1Shifts[12], 1804603682);
            d = Ff(d, a, b, c, chunks[i + 13], R1Shifts[13], -40341101);
            c = Ff(c, d, a, b, chunks[i + 14], R1Shifts[14], -1502002290);
            b = Ff(b, c, d, a, chunks[i + 15], R1Shifts[15], 1236535329);
            a = Gg(a, b, c, d, chunks[i + 1], R2Shifts[0], -165796510);
            d = Gg(d, a, b, c, chunks[i + 6], R2Shifts[1], -1069501632);
            c = Gg(c, d, a, b, chunks[i + 11], R2Shifts[2], 643717713);
            b = Gg(b, c, d, a, chunks[i + 0], R2Shifts[3], -373897302);
            a = Gg(a, b, c, d, chunks[i + 5], R2Shifts[4], -701558691);
            d = Gg(d, a, b, c, chunks[i + 10], R2Shifts[5], 38016083);
            c = Gg(c, d, a, b, chunks[i + 15], R2Shifts[6], -660478335);
            b = Gg(b, c, d, a, chunks[i + 4], R2Shifts[7], -405537848);
            a = Gg(a, b, c, d, chunks[i + 9], R2Shifts[8], 568446438);
            d = Gg(d, a, b, c, chunks[i + 14], R2Shifts[9], -1019803690);
            c = Gg(c, d, a, b, chunks[i + 3], R2Shifts[10], -187363961);
            b = Gg(b, c, d, a, chunks[i + 8], R2Shifts[11], 1163531501);
            a = Gg(a, b, c, d, chunks[i + 13], R2Shifts[12], -1444681467);
            d = Gg(d, a, b, c, chunks[i + 2], R2Shifts[13], -51403784);
            c = Gg(c, d, a, b, chunks[i + 7], R2Shifts[14], 1735328473);
            b = Gg(b, c, d, a, chunks[i + 12], R2Shifts[15], -1926607734);
            a = Hh(a, b, c, d, chunks[i + 5], R3Shifts[0], -378558);
            d = Hh(d, a, b, c, chunks[i + 8], R3Shifts[1], -2022574463);
            //line below uses _r2Shifts[2] where as MD5 would use _r3Shifts[2]
            c = Hh(c, d, a, b, chunks[i + 11], R2Shifts[2], 1839030562);
            b = Hh(b, c, d, a, chunks[i + 14], R3Shifts[3], -35309556);
            a = Hh(a, b, c, d, chunks[i + 1], R3Shifts[4], -1530992060);
            d = Hh(d, a, b, c, chunks[i + 4], R3Shifts[5], 1272893353);
            c = Hh(c, d, a, b, chunks[i + 7], R3Shifts[6], -155497632);
            b = Hh(b, c, d, a, chunks[i + 10], R3Shifts[7], -1094730640);
            a = Hh(a, b, c, d, chunks[i + 13], R3Shifts[8], 681279174);
            d = Hh(d, a, b, c, chunks[i + 0], R3Shifts[9], -358537222);
            c = Hh(c, d, a, b, chunks[i + 3], R3Shifts[10], -722521979);
            b = Hh(b, c, d, a, chunks[i + 6], R3Shifts[11], 76029189);
            a = Hh(a, b, c, d, chunks[i + 9], R3Shifts[12], -640364487);
            d = Hh(d, a, b, c, chunks[i + 12], R3Shifts[13], -421815835);
            c = Hh(c, d, a, b, chunks[i + 15], R3Shifts[14], 530742520);
            b = Hh(b, c, d, a, chunks[i + 2], R3Shifts[15], -995338651);
            a = Ii(a, b, c, d, chunks[i + 0], R4Shifts[0], -198630844);
            d = Ii(d, a, b, c, chunks[i + 7], R4Shifts[1], 1126891415);
            c = Ii(c, d, a, b, chunks[i + 14], R4Shifts[2], -1416354905);
            b = Ii(b, c, d, a, chunks[i + 5], R4Shifts[3], -57434055);
            a = Ii(a, b, c, d, chunks[i + 12], R4Shifts[4], 1700485571);
            d = Ii(d, a, b, c, chunks[i + 3], R4Shifts[5], -1894986606);
            c = Ii(c, d, a, b, chunks[i + 10], R4Shifts[6], -1051523);
            b = Ii(b, c, d, a, chunks[i + 1], R4Shifts[7], -2054922799);
            a = Ii(a, b, c, d, chunks[i + 8], R4Shifts[8], 1873313359);
            d = Ii(d, a, b, c, chunks[i + 15], R4Shifts[9], -30611744);
            c = Ii(c, d, a, b, chunks[i + 6], R4Shifts[10], -1560198380);
            b = Ii(b, c, d, a, chunks[i + 13], R4Shifts[11], 1309151649);
            a = Ii(a, b, c, d, chunks[i + 4], R4Shifts[12], -145523070);
            d = Ii(d, a, b, c, chunks[i + 11], R4Shifts[13], -1120210379);
            c = Ii(c, d, a, b, chunks[i + 2], R4Shifts[14], 718787259);
            b = Ii(b, c, d, a, chunks[i + 9], R4Shifts[15], -343485551);
            //This line is doubled for some reason, line below is not in the MD5 version
            b = Ii(b, c, d, a, chunks[i + 9], R4Shifts[15], -343485551);
            a = Add(a, tempA);
            b = Add(b, tempB);
            c = Add(c, tempC);
            d = Add(d, tempD);
        }

        return NumberToHex(a) + NumberToHex(b) + NumberToHex(c) + NumberToHex(d);
    }

    private static int Ff(int a, int b, int c, int d, int x, int s, int t) {
        return Cmn((b & c) | ((~b) & d), a, b, x, s, t);
    }

    private static int Gg(int a, int b, int c, int d, int x, int s, int t) {
        return Cmn((b & d) | (c & (~d)), a, b, x, s, t);
    }

    private static int Hh(int a, int b, int c, int d, int x, int s, int t) {
        return Cmn(b ^ c ^ d, a, b, x, s, t);
    }

    private static int Ii(int a, int b, int c, int d, int x, int s, int t) {
        return Cmn(c ^ (b | (~d)), a, b, x, s, t);
    }

    private static int Cmn(int q, int a, int b, int x, int s, int t) {
        return Add(BitwiseRotate(Add(Add(a, q), Add(x, t)), s), b);
    }

    private static int Add(int x, int y) {
        int lsw = (x & 0xFFFF) + (y & 0xFFFF);
        int msw = (x >> 16) + (y >> 16) + (lsw >> 16);

        return (msw << 16) | (lsw & 0xFFFF);
    }

    private static int BitwiseRotate(int x, int c) {
        long unsigned = Long.parseLong(Integer.toUnsignedString(x));
        return (int) ((x << c) | (unsigned >> (32 - c)));
    }

    private static int[] ChunkInput(String input) {
        int numberOfBlocks = ((input.length() + 8) >> 6) + 1;
        int[] blocks = new int[numberOfBlocks * 16];

        for (int i = 0; i < input.length(); i++) {
            blocks[i >> 2] |= input.charAt(i) << ((i % 4) * 8);
        }

        blocks[input.length() >> 2] |= 0x80 << ((input.length() % 4) * 8);
        blocks[numberOfBlocks * 16 - 2] = input.length() * 8;

        return blocks;
    }

    private static String NumberToHex(int number) {
        //BREAKING HERE
        StringBuilder result = new StringBuilder();
        for (int j = 0; j <= 3; j++) {
            result.append(HexCharacters.charAt(((number >> (j * 8 + 4)) & 0x0F)));
            result.append(HexCharacters.charAt(((number >> (j * 8)) & 0x0F)));
        }

        return result.toString();
    }
}
