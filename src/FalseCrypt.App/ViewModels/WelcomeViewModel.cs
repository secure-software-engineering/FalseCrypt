using System.Collections.Generic;
using System.ComponentModel.Composition;
using System.IO;
using System.Linq;
using System.Windows.Input;
using Caliburn.Micro;
using ModernApplicationFramework.Controls.Dialogs.Native;
using ModernApplicationFramework.Input.Command;

namespace FalseCrypt.App.ViewModels
{
    [Export(typeof(WelcomeViewModel))]
    public class WelcomeViewModel : Screen
    {
        public ICommand EncryptFileCommand { get; } = new Command(EncryptFile, () => true);

        public ICommand EncryptFolderCommand { get; } = new Command(EncryptFolder, () => true);

        public ICommand DecryptFileCommand { get; } = new Command(DecryptFile, () => true);

        public ICommand DecryptFolderCommand { get; } = new Command(DecryptFolder, () => true);

        private static void EncryptFile()
        {
            var files = GetFiles();
            if (files == null || !files.Any())
                return;

            foreach (var file in files)
            {
                if (!File.Exists(file))
                    continue;
            }
        }

        private static void EncryptFolder()
        {
            var folder = GetFolder();
            if (string.IsNullOrEmpty(folder))
                return;
        }

        private static void DecryptFile()
        {
            var files = GetFiles("falsecrypt File (*.falsecrypt)|*.falsecrypt");
            if (files == null || !files.Any())
                return;

            foreach (var file in files)
            {
                if (!File.Exists(file))
                    continue;
            }
        }

        private static void DecryptFolder()
        {
            var folder = GetFolder();
            if (string.IsNullOrEmpty(folder))
                return;
        }

        private static IReadOnlyCollection<string> GetFiles(string filter = null)
        {
            var fd = new NativeOpenFileDialog
            {
                Multiselect = true,
                CheckFileExists = true,
            };

            if (!string.IsNullOrEmpty(filter))
                fd.Filter = filter;

            var result = fd.ShowDialog();
            if (result == null || !result.Value)
                return null;
            return fd.FileNames;
        }

        private static string GetFolder()
        {
            var fd = new NativeFolderBrowserDialog();
            var result = fd.ShowDialog();
            if (result == null || !result.Value)
                return null;

            return fd.SelectedPath;
        }
    }
}
