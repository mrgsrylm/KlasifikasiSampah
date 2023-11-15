import Layout from '../components/Layout';
import Hero from '../components/heroTentang/Hero';
import TechStacks from '../components/techStacksSection/TechStacks';
import Credits from '../components/creditsSection/Credits';

export default function Tentang() {
  return (
    <Layout>
      <Hero />
      <TechStacks />
      <Credits />
    </Layout>
  );
}