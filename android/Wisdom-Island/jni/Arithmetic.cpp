/*
 * File         : Arithmetic.cpp
 * Date         : 2007-07-12
 * Author       : Keqin Su
 * Copyright    : City Hotspot Co., Ltd.
 * Description  : Class for Arithmetic
 */

#include <malloc.h>
#include <string.h>
#include <ctype.h>
#include "Arithmetic.h"

char Arithmetic::hex[16] = {
	'0','1','2','3','4','5','6','7',
	'8','9','A','B','C','D','E','F'
};

int Arithmetic::encode[64] = {
  'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
  'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
  'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
  'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
  'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
  'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
  'w', 'x', 'y', 'z', '0', '1', '2', '3',
  '4', '5', '6', '7', '8', '9', '+', '/'
};

char Arithmetic::rstr[128] = {
	0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
	0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
	0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,  62,   0,   0,   0,  63,
	52, 53,  54,  55,  56,  57,  58,  59,  60,  61,   0,   0,   0,   0,   0,   0,
	0,   0,   1,   2,   3,   4,   5,   6,   7,   8,   9,  10,  11,  12,  13,  14,
	15, 16,  17,  18,  19,  20,  21,  22,  23,  24,  25,   0,   0,   0,   0,   0,
	0,  26,  27,  28,  29,  30,  31,  32,  33,  34,  35,  36,  37,  38,  39,  40,
	41, 42,  43,  44,  45,  46,  47,  48,  49,  50,  51,   0,   0,   0,   0,   0
};

unsigned long Arithmetic::crc_32_tab[] = { /* CRC polynomial 0xedb88320 */
    0x00000000, 0x77073096, 0xee0e612c, 0x990951ba, 0x076dc419, 0x706af48f, 0xe963a535, 0x9e6495a3,
    0x0edb8832, 0x79dcb8a4, 0xe0d5e91e, 0x97d2d988, 0x09b64c2b, 0x7eb17cbd, 0xe7b82d07, 0x90bf1d91,
    0x1db71064, 0x6ab020f2, 0xf3b97148, 0x84be41de, 0x1adad47d, 0x6ddde4eb, 0xf4d4b551, 0x83d385c7,
    0x136c9856, 0x646ba8c0, 0xfd62f97a, 0x8a65c9ec, 0x14015c4f, 0x63066cd9, 0xfa0f3d63, 0x8d080df5,
    0x3b6e20c8, 0x4c69105e, 0xd56041e4, 0xa2677172, 0x3c03e4d1, 0x4b04d447, 0xd20d85fd, 0xa50ab56b,
    0x35b5a8fa, 0x42b2986c, 0xdbbbc9d6, 0xacbcf940, 0x32d86ce3, 0x45df5c75, 0xdcd60dcf, 0xabd13d59,
    0x26d930ac, 0x51de003a, 0xc8d75180, 0xbfd06116, 0x21b4f4b5, 0x56b3c423, 0xcfba9599, 0xb8bda50f,
    0x2802b89e, 0x5f058808, 0xc60cd9b2, 0xb10be924, 0x2f6f7c87, 0x58684c11, 0xc1611dab, 0xb6662d3d,
    0x76dc4190, 0x01db7106, 0x98d220bc, 0xefd5102a, 0x71b18589, 0x06b6b51f, 0x9fbfe4a5, 0xe8b8d433,
    0x7807c9a2, 0x0f00f934, 0x9609a88e, 0xe10e9818, 0x7f6a0dbb, 0x086d3d2d, 0x91646c97, 0xe6635c01,
    0x6b6b51f4, 0x1c6c6162, 0x856530d8, 0xf262004e, 0x6c0695ed, 0x1b01a57b, 0x8208f4c1, 0xf50fc457,
    0x65b0d9c6, 0x12b7e950, 0x8bbeb8ea, 0xfcb9887c, 0x62dd1ddf, 0x15da2d49, 0x8cd37cf3, 0xfbd44c65,
    0x4db26158, 0x3ab551ce, 0xa3bc0074, 0xd4bb30e2, 0x4adfa541, 0x3dd895d7, 0xa4d1c46d, 0xd3d6f4fb,
    0x4369e96a, 0x346ed9fc, 0xad678846, 0xda60b8d0, 0x44042d73, 0x33031de5, 0xaa0a4c5f, 0xdd0d7cc9,
    0x5005713c, 0x270241aa, 0xbe0b1010, 0xc90c2086, 0x5768b525, 0x206f85b3, 0xb966d409, 0xce61e49f,
    0x5edef90e, 0x29d9c998, 0xb0d09822, 0xc7d7a8b4, 0x59b33d17, 0x2eb40d81, 0xb7bd5c3b, 0xc0ba6cad,
    0xedb88320, 0x9abfb3b6, 0x03b6e20c, 0x74b1d29a, 0xead54739, 0x9dd277af, 0x04db2615, 0x73dc1683,
    0xe3630b12, 0x94643b84, 0x0d6d6a3e, 0x7a6a5aa8, 0xe40ecf0b, 0x9309ff9d, 0x0a00ae27, 0x7d079eb1,
    0xf00f9344, 0x8708a3d2, 0x1e01f268, 0x6906c2fe, 0xf762575d, 0x806567cb, 0x196c3671, 0x6e6b06e7,
    0xfed41b76, 0x89d32be0, 0x10da7a5a, 0x67dd4acc, 0xf9b9df6f, 0x8ebeeff9, 0x17b7be43, 0x60b08ed5,
    0xd6d6a3e8, 0xa1d1937e, 0x38d8c2c4, 0x4fdff252, 0xd1bb67f1, 0xa6bc5767, 0x3fb506dd, 0x48b2364b,
    0xd80d2bda, 0xaf0a1b4c, 0x36034af6, 0x41047a60, 0xdf60efc3, 0xa867df55, 0x316e8eef, 0x4669be79,
    0xcb61b38c, 0xbc66831a, 0x256fd2a0, 0x5268e236, 0xcc0c7795, 0xbb0b4703, 0x220216b9, 0x5505262f,
    0xc5ba3bbe, 0xb2bd0b28, 0x2bb45a92, 0x5cb36a04, 0xc2d7ffa7, 0xb5d0cf31, 0x2cd99e8b, 0x5bdeae1d,
    0x9b64c2b0, 0xec63f226, 0x756aa39c, 0x026d930a, 0x9c0906a9, 0xeb0e363f, 0x72076785, 0x05005713,
    0x95bf4a82, 0xe2b87a14, 0x7bb12bae, 0x0cb61b38, 0x92d28e9b, 0xe5d5be0d, 0x7cdcefb7, 0x0bdbdf21,
    0x86d3d2d4, 0xf1d4e242, 0x68ddb3f8, 0x1fda836e, 0x81be16cd, 0xf6b9265b, 0x6fb077e1, 0x18b74777,
    0x88085ae6, 0xff0f6a70, 0x66063bca, 0x11010b5c, 0x8f659eff, 0xf862ae69, 0x616bffd3, 0x166ccf45,
    0xa00ae278, 0xd70dd2ee, 0x4e048354, 0x3903b3c2, 0xa7672661, 0xd06016f7, 0x4969474d, 0x3e6e77db,
    0xaed16a4a, 0xd9d65adc, 0x40df0b66, 0x37d83bf0, 0xa9bcae53, 0xdebb9ec5, 0x47b2cf7f, 0x30b5ffe9,
    0xbdbdf21c, 0xcabac28a, 0x53b39330, 0x24b4a3a6, 0xbad03605, 0xcdd70693, 0x54de5729, 0x23d967bf,
    0xb3667a2e, 0xc4614ab8, 0x5d681b02, 0x2a6f2b94, 0xb40bbe37, 0xc30c8ea1, 0x5a05df1b, 0x2d02ef8d
};

#define UPDC32(octet, crc) (crc_32_tab[((crc) ^ (octet)) & 0xff] ^ ((crc) >> 8))

int Arithmetic::TeaEncode(char* p_in, int i_in_len, char* p_key, char* p_out)
{
    int i_fill_len, i_out_len,i;
	char key[16]={0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	if(p_in == NULL || i_in_len <= 0){
		return -1;
	}
	i_fill_len = (i_in_len & 0x07);
	i_out_len = i_in_len;
	if(i_fill_len != 0){
		i_fill_len = 8 - i_fill_len;
		i_out_len += i_fill_len;
	}
	if(p_out == NULL){
		return i_out_len;
	}
	memcpy(p_out, p_in, i_in_len);
	memset(p_out + i_in_len, 0, i_fill_len);
	memcpy((void*)key, p_key, (strlen(p_key) > 16) ? 16 : strlen(p_key));
	for(i = 0; i < i_out_len; i += 8){
		encipher(&p_out[i], key);
	}
	return i_out_len;
}

int Arithmetic::TeaDecode(char* p_in, int i_in_len, char* p_key, char* p_out)
{
    int i;
	char key[16]={0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	if(p_in == NULL || i_in_len <= 0){
		return -1;
	}
	if(i_in_len & 0x07L){
		return -2;
	}
	if(p_out == NULL){
		return i_in_len;
	}
	memcpy(p_out, p_in, i_in_len);
	memcpy((void*)key, p_key, (strlen(p_key) > 16) ? 16 : strlen(p_key));
	for(i = 0; i < i_in_len; i += 8){
		decipher(&p_out[i], key);
	}
	return i_in_len;
}

int Arithmetic::Base64Encode(const char* data, int length, char** code)
{
    const unsigned char *s, *end;
	unsigned char* buf;
	unsigned int x;
	int n;
	int i, j;

	if (length == 0){
		return 0;
	}
	end = (unsigned char*)data + length - 3;
	buf = (unsigned char *)malloc (4 * ((length + 2) / 3) + 1);
	if (buf == NULL){
		return -1;
	}
	n = 0;
	for (s = (unsigned char *)data; s < end;){
		x = *s++ << 24;
		x |= *s++ << 16;
		x |= *s++ << 8;
		*buf++ = encode[x >> 26];
		x <<= 6;
		*buf++ = encode[x >> 26];
		x <<= 6;
		*buf++ = encode[x >> 26];
		x <<= 6;
		*buf++ = encode[x >> 26];
		n += 4;
    }
	end += 3;
	x = 0;
	for (i = 0; s < end; i++){
		x |= *s++ << (24 - 8 * i);
	}
	for (j = 0; j < 4; j++){
		if (8 * i >= 6 * j){
			*buf++ = encode [x >> 26];
			x <<= 6;
			n++;
		}else{
			*buf++ = '=';
			n++;
		}
    }
	*buf = 0;
	*code = (char*)buf - n;
	return n;
}

int Arithmetic::Base64Decode(const char* data, int length, char* code)
{
    int i = 0;
	int j = 0;
	int l = length;
	int k;

	if (length == 0){
		return 0;
	}

	while (i < l){
		while (i < l && (data[i] == 13 || data[i] == 10)){
			i++;
		}
		if (i < l){
			unsigned char b1 = (rstr[(int)data[i]] << 2 & 0xfc) + (rstr[(int)data[i + 1]] >> 4 & 0x03);
			code[j++] = b1;
			if (data[i + 2] != '='){
				unsigned char b2 = (rstr[(int)data[i + 1]] << 4 & 0xf0) + (rstr[(int)data[i + 2]] >> 2 & 0x0f);
				code[j++] = b2;
			}
			if (data[i + 3] != '='){
				unsigned char b3 = (rstr[(int)data[i + 2]] << 6 & 0xc0) + rstr[(int)data[i + 3]];
				code[j++] = b3;
			}
			i += 4;
		}
		k = j;
	}
	return k;
}

int Arithmetic::AsciiToHex(const char* data, int i_in_len, char* code)
{
    int len = 0;
    unsigned char* p = (unsigned char*)data;
    for (int i = 0; i < i_in_len; i++){
        code[len++] = hex[p[i] >> 4];
        //code[len++] = hex[p[i] % 16];
        code[len++] = hex[p[i] & (16 - 1)];
    }
    code[len] = '\0';
    return len;
}

int Arithmetic::HexToAscii(const char* data, int i_in_len, char* code)
{
    int len = 0;
	unsigned char* p = (unsigned char*)data;
    char q[3] = {0, 0, 0};
    for (int i = 0; i < i_in_len; i+=2){
        q[0] = p[i];
        q[1] = p[i + 1];
	    code[len++] = (char)strtoul(q, '\0', 16);
    }
    return len;
}

int Arithmetic::encode_url(const char* data, int length, char* code)
{
    int i, len = 0;
    unsigned char* p = (unsigned char*)data;
    for (i = 0; i < (int)length; i++){
        if (isalnum(p[i])){
            code[len++] = p[i];
        }else if (isspace(p[i])){
            code[len++] = '+';
        }else{
            code[len++] = '%';
            code[len++] = hex[p[i]>>4];
            code[len++] = hex[p[i]%16];
        }
    }
    code[len] = '\0';
    return len;
}

int Arithmetic::decode_url(const char* data, int length, char* code)
{
    int i, len = 0;
    char s[3] = {0,0,0};
    char* p = (char*)data;
    unsigned long ul;
    for (i = 0; i < (int)length; i++){
        if (p[i] == '+'){
            code[len++] = ' ';
        }else if (p[i] == '%'){
            s[0] = p[i+1];
            s[1] = p[i+2];
            ul = strtoul(s,NULL,16);
            code[len++] = (char)ul;
            i += 2;
        }else{
            code[len++] = p[i];
        }
    }
    code[len] = '\0';
    return len;
}

int Arithmetic::encode_urlspecialchar(const char* data, int length, char* code)
{
    int i, len = 0;
    char* p = (char*)data;
    for (i = 0; i < (int)length; i++){
        if (p[i] == '&'){
            memcpy(code+len, "&amp;", 5);
            len += 5;
        }else if (p[i] == '<'){
            memcpy(code+len, "&lt;", 4);
            len += 4;
        }else if (p[i] == '>'){
            memcpy(code+len, "&gt;", 4);
            len += 4;
        }else if (p[i] == '"'){
            memcpy(code+len, "&quot;", 6);
            len += 6;
        }else if (p[i] == '\''){
            memcpy(code+len, "&apos;", 6);
            len += 6;
        }else{
            code[len++] = p[i];
        }
    }
    code[len] = '\0';
    return len;
}

int Arithmetic::decode_urlspecialchar(const char* data, int length, char* code)
{
    int i, len = 0;
    char* p = (char*)data;
    for (i = 0; i < (int)length; i++){
        if (p[i] == '&'){
            switch (p[i+1]){
                case 'a':
                    if (p[i+2]=='m' && p[i+3]=='p' && p[i+4]==';'){
                        code[len++] = '&';
                        i += 4;
                    }else if (p[i+2]=='p' && p[i+3]=='o' && p[i+4]=='s'&& p[i+5]==';'){
                        code[len++] = '\'';
                        i += 5;
                    }
                    break;
                case 'l':
                    if (p[i+2]=='t' && p[i+3]==';'){
                        code[len++] = '<';
                        i += 3;
                    }
                    break;
                case 'g':
                    if (p[i+2]=='t' && p[i+3]==';'){
                        code[len++] = '>';
                        i += 3;
                    }
                    break;
                case 'q':
                    if (p[i+2]=='u' && p[i+3]=='o' && p[i+4]=='t' && p[i+5]==';'){
                        code[len++] = '"';
                        i += 5;
                    }
                    break;
            }
        }else{
            code[len++] = p[i];
        }
    }
    code[len] = '\0';
    return len;
}

unsigned long Arithmetic::MakeCRC32(char* data, int i_in_len)
{
    register unsigned long rulCrc32 = 0xFFFFFFFF;
    for (int i = 0; i < i_in_len; i++){
        rulCrc32 = UPDC32(data[i], rulCrc32);
    }

    return ~rulCrc32;
}

bool Arithmetic::String2Mac(char* pstr, char* Mac)
{
    if (strlen(pstr) != strlen("00:00:00:00:00:00")) {
        return false;
    }

    int iarr[6] = {0};
    sscanf(pstr, "%02X:%02X:%02X:%02X:%02X:%02X", &iarr[0], &iarr[1], &iarr[2], &iarr[3], &iarr[4], &iarr[5]);
    for (int i = 0; i < 6; i++) {
        Mac[i] = (char)iarr[i];
    }

    return true;
}

bool Arithmetic::Mac2String(char* pstr, char* Mac)
{
    sprintf(pstr, "%02X:%02X:%02X:%02X:%02X:%02X", (unsigned char)Mac[0], (unsigned char)Mac[1], (unsigned char)Mac[2],
            (unsigned char)Mac[3], (unsigned char)Mac[4], (unsigned char)Mac[5]);

    return true;
}

/*******************protected function*************************/

void Arithmetic::encipher(void* aData, const void* aKey)
{
    const long cnDelta = 0x9E3779B9;
    register long y = ((long*)aData)[0], z = ((long*)aData)[1];
    register long sum = 0;
    long a = ((long*)aKey)[0], b = ((long*)aKey)[1];
    long c = ((long*)aKey)[2], d = ((long*)aKey)[3];
    int n = 32;

    while (n-- > 0){
        sum += cnDelta;
        y += (z << 4) + a ^ z + sum ^ (z >> 5) + b;
        z += (y << 4) + c ^ y + sum ^ (y >> 5) + d;
    }
    ((long*)aData)[0] = y;
    ((long*)aData)[1] = z;
}

void Arithmetic::decipher(void* aData, const void* aKey)
{
    const long cnDelta = 0x9E3779B9;
    register long y = ((long*)aData)[0], z = ((long*)aData)[1];
    register long sum = 0xC6EF3720;
    long a = ((long*)aKey)[0], b = ((long*)aKey)[1];
    long c = ((long*)aKey)[2], d = ((long*)aKey)[3];
    int n = 32;

    while (n-- > 0){
        z -= (y << 4) + c ^ y + sum ^ (y >> 5) + d;
        y -= (z << 4) + a ^ z + sum ^ (z >> 5) + b;
        sum -= cnDelta;
    }
    ((long*)aData)[0] = y;
    ((long*)aData)[1] = z;
}

