using ModernApplicationFramework.Basics.Services;

namespace FalseCrypt.App
{
    public sealed class AppBootstrapper : Bootstrapper
    {
        public AppBootstrapper() : base(false)
        {
            Initialize();
        }
    }
}
