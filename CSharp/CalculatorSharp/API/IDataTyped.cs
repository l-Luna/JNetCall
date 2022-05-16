using System;
using System.Collections.Generic;

namespace Example.API
{
    public interface IDataTyped : IDisposable
    {
        string ToSimpleText(sbyte y, short s, int i, long l, float f, double d,
            bool b, char c, string t);

        string ToArrayText(byte[] y, short[] s, int[] i, long[] l, float[] f, double[] d,
            bool[] b, char[] c, string[] t);
        
        int GetLineCount(string[] lines);
        
        long GetFileSize(string path);

        byte[] AllocateBytes(int size, byte value);

        ISet<string> GetUnique(IList<string> lines, bool withTrim);

        IList<string> GetDouble(ISet<string> lines);

        IDictionary<string, int> GetSystemVariables(DateTime dts, TimeSpan dur, 
            IDictionary<string, int> parent);
    }
}